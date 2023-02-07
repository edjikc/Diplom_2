import client.IngredientClient;
import client.OrderClient;
import client.UserClient;
import generator.OrderGenerator;
import generator.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.Ingredient;
import pojo.Order;
import pojo.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.http.HttpStatus.*;

public class OrderTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private IngredientClient ingredientClient;

    private String accessToken = "";
    private User user;

    @Before
    public void setup() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredientClient = new IngredientClient();
        user = UserGenerator.createUser();
        userClient.deleteUserIfExist(user);
    }

    @Test
    @DisplayName("Создание заказа")
    public void createOrder() {
        accessToken = userClient.createAndLogin(user);
        List<String> ingredients = getIngredients()
                .stream()
                .limit(2)
                .map(Ingredient::getId)
                .collect(Collectors.toList());
        Order order = OrderGenerator.generateOrderByIngredients(ingredients);

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));

        Response orders = orderClient.getOrders(accessToken);
        orders.then()
                .statusCode(SC_OK)
                .and()
                .body("orders.size()", Matchers.is(1));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderUnauthorized() {
        List<Ingredient> ingredients = getIngredients()
                .stream()
                .limit(1)
                .collect(Collectors.toList());
        Order order = OrderGenerator.generateOrderByIngredients(
                ingredients.stream().map(Ingredient::getId).collect(Collectors.toList())
        );

        Response orderUnauthorized = orderClient.createOrderUnauthorized(order);
        orderUnauthorized.then()
                .statusCode(SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        accessToken = userClient.createAndLogin(user);
        Order order = OrderGenerator.generateEmptyOrder();
        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void createOrderUnauthorizedWithoutIngredients() {
        Order order = OrderGenerator.generateEmptyOrder();
        orderClient.createOrderUnauthorized(order)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неправильным хешем ингредиентов")
    public void createOrderWithIncorrectIngredients() {
        accessToken = userClient.createAndLogin(user);
        List<String> ingredients = getIngredients()
                .stream()
                .limit(1)
                .map(ingredient -> ingredient.getId() + UUID.randomUUID())
                .collect(Collectors.toList());
        Order order = OrderGenerator.generateOrderByIngredients(ingredients);
        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и с неправильным хешем ингредиентов")
    public void createOrderUnauthorizedWithIncorrectIngredients() {
        accessToken = userClient.createAndLogin(user);
        List<String> ingredients = getIngredients()
                .stream()
                .limit(1)
                .map(ingredient -> ingredient.getId() + UUID.randomUUID())
                .collect(Collectors.toList());
        Order order = OrderGenerator.generateOrderByIngredients(ingredients);
        orderClient.createOrderUnauthorized(order)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Получение заказов пользователя")
    public void getUserOrders() {
        accessToken = userClient.createAndLogin(user);
        List<String> ingredients = getIngredients()
                .stream()
                .limit(2)
                .map(Ingredient::getId)
                .collect(Collectors.toList());
        Order order = OrderGenerator.generateOrderByIngredients(ingredients);
        Response orderResponse = orderClient.createOrder(order, accessToken);
        orderResponse.then().statusCode(SC_OK);
        order.setName(orderResponse.path("name"));
        order.setNumber(orderResponse.jsonPath().getLong("order.number"));

        Response getOrdersResponse = orderClient.getOrders(accessToken);
        getOrdersResponse.then().statusCode(SC_OK);

        List<Order> orders = getOrdersResponse.jsonPath().getList("orders", Order.class);
        Assert.assertEquals(1, orders.size());
        Assert.assertEquals(order, orders.get(0));
    }

    @Test
    @DisplayName("Полечение заказов без авторизации")
    public void getOrdersUnauthorized() {
        orderClient.getOrdersUnauthorized()
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("You should be authorised"));
    }

    private List<Ingredient> getIngredients() {
        Response ingredients = ingredientClient.getIngredients();
        ingredients.then().statusCode(SC_OK);
        return ingredients.getBody().jsonPath().getList("data", Ingredient.class);
    }

    @After
    public void clean() {
        userClient.deleteUser(accessToken);
    }
}
