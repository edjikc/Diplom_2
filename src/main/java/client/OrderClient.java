package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import pojo.Order;

public class OrderClient extends Client {
    private static final String ORDER_PATH = "/api/orders";

    @Step("Отправка запроса на создание нового заказа по объекту {order}, с токеном {token}")
    public Response createOrder(Order order, String token) {
        return buildRequest()
                .header("Authorization", token)
                .body(order)
                .post(ORDER_PATH);
    }

    @Step("Отправка запроса на создание нового заказа по объекту {order}, без авторизации")
    public Response createOrderUnauthorized(Order order) {
        return buildRequest()
                .body(order)
                .post(ORDER_PATH);
    }

    @Step("Отправка запроса на получение заказов по токену {token}")
    public Response getOrders(String token) {
        return buildRequest()
                .header("Authorization", token)
                .get(ORDER_PATH);
    }

    @Step("Отправка запроса на получение заказов без авторизации")
    public Response getOrdersUnauthorized() {
        return buildRequest()
                .get(ORDER_PATH);
    }
}
