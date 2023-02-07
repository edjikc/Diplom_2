import client.UserClient;
import generator.CredentialsGenerator;
import generator.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class LoginUserTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setup() {
        userClient = new UserClient();
        user = UserGenerator.createUser();
        userClient.deleteUserIfExist(user);
        createUser(user);
    }

    @Test
    @DisplayName("Авторизация существующим пользователем")
    public void loginExistingUser() {
        Response loginResponse = userClient.loginUser(CredentialsGenerator.generateCredentialsFromUser(user));
        loginResponse.then()
                .statusCode(SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));
        accessToken = loginResponse.path("accessToken");
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    public void loginInvalid() {
        Response response = userClient.loginUser(CredentialsGenerator.generateFromEmailAndPass(user.getEmail(), user.getPassword() + Math.random()));
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", Matchers.equalTo(false));
        loginExistingUser();
    }

    private void createUser(User user) {
        Response newUser = userClient.createNewUser(user);
        newUser.then().statusCode(SC_OK);
    }

    @After
    public void clean() {
        userClient.deleteUser(accessToken);
    }
}
