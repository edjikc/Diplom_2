package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

public class IngredientClient extends Client {
    private static final String INGREDIENT_PATH = "/api/ingredients";

    @Step("Отправка запроса на получение ингредиентов")
    public Response getIngredients() {
        return buildRequest()
                .get(INGREDIENT_PATH);
    }
}
