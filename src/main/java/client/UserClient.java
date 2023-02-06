package client;

import generator.CredentialsGenerator;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import pojo.Credentials;
import pojo.User;

import static org.apache.http.HttpStatus.SC_OK;

public class UserClient extends Client {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String USER_PATH = "/api/auth/user";
    private static final String LOGIN_PATH = "/api/auth/login";


    @Step("Отправка запроса на создание нового пользователя по объекту {user}")
    public Response createNewUser(User user) {
        return buildRequest()
                .body(user)
                .post(REGISTER_PATH);
    }

    @Step("Отправка запроса на создание нового пользователя по объекту {user}")
    public Response getUser(String token) {
        return buildRequest()
                .header("Authorization", token)
                .get(USER_PATH);
    }

    @Step("Отправка запроса на удаление пользователя по объекту {user}")
    public Response deleteUser(String token) {
        return buildRequest()
                .header("Authorization", token)
                .delete(USER_PATH);
    }

    @Step("Логин пользователя по объекту {creds}")
    public Response loginUser(Credentials credentials) {
        return buildRequest()
                .body(credentials)
                .post(LOGIN_PATH);
    }

    @Step("Запрос на удаление пользователя по объекту {user}, если пользователь существует")
    public void deleteUserIfExist(User user) {
        Response response = loginUser(CredentialsGenerator.generateCredentialsFromUser(user));
        Boolean success = response.getBody().path("success");
        if (success) {
            deleteUser(response.path("accessToken"));
        }
    }

    @Step("Запрос на обновление пользователя по объекту {newUser} с токеном {token}")
    public Response updateUserFields(User newUser, String token){
        return buildRequest()
                .header("Authorization", token)
                .body(newUser)
                .patch(USER_PATH);
    }
    @Step("Запрос на обновление пользователя по объекту {user} без авторизации")
    public Response updateUnauthorizedUser(User user){
        return buildRequest()
                .body(user)
                .patch(USER_PATH);
    }

    public String createAndLogin(User user) {
        Response newUser = createNewUser(user);
        newUser.then().statusCode(SC_OK);
        return newUser.path("accessToken");
    }
}
