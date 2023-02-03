import client.UserClient;
import generator.CredentialsGenerator;
import generator.UserGenerator;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import java.util.UUID;

import static org.apache.http.HttpStatus.*;

public class UpdateUserTest {


    private final String NEW_NAME = "NEW_TEST_NAME";
    private final String NEW_PASS = "NEW_TEST_PASS";
    private UserClient userClient;
    private User user;
    private String accessToken = "";

    @Before
    public void setup() {
        userClient = new UserClient();
        user = UserGenerator.createUser();
        userClient.deleteUserIfExist(user);
    }

    @Test
    public void updateNameUserField() {
        accessToken = userClient.createAndLogin(user);
        user.setName(NEW_NAME);
        Response response = userClient.updateUserFields(user, accessToken);
        response.then()
                .statusCode(SC_OK)
                .and()
                .body("success", Matchers.equalTo(true))
                .and()
                .body("user.name", Matchers.equalTo(user.getName()));
        userClient.getUser(accessToken)
                .then()
                .statusCode(SC_OK)
                .and()
                .body("user.name", Matchers.equalTo(user.getName()));
    }

    @Test
    public void updatePassUserField() {
        accessToken = userClient.createAndLogin(user);

        user.setPassword(NEW_PASS);
        userClient.updateUserFields(user, accessToken)
                .then()
                .statusCode(SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));

        Response response = userClient.loginUser(CredentialsGenerator.generateCredentialsFromUser(user));
        response.then()
                .statusCode(SC_OK);
        accessToken = response.path("accessToken");
    }

    @Test
    public void updateEmailField(){
        accessToken = userClient.createAndLogin(user);

        user.setEmail(UUID.randomUUID() + user.getEmail());
        userClient.updateUserFields(user, accessToken)
                .then()
                .statusCode(SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));

        Response response = userClient.loginUser(CredentialsGenerator.generateCredentialsFromUser(user));
        response.then()
                .statusCode(SC_OK);
        accessToken = response.path("accessToken");
    }

    @Test
    public void updateExistingEmailField(){
        accessToken = userClient.createAndLogin(user);


        User newUser = UserGenerator.createUserByEmail(UUID.randomUUID() + user.getEmail());
        userClient.deleteUserIfExist(newUser);
        String newAccessToken = userClient.createAndLogin(newUser);

        user.setEmail(newUser.getEmail());
        userClient.updateUserFields(user, accessToken)
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("User with such email already exists"));

        userClient.deleteUser(newAccessToken);
    }

    @Test
    public void updateUnauthorizedUser(){
        User unauthorizedUser = UserGenerator.createUser();
        userClient.updateUnauthorizedUser(unauthorizedUser)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("You should be authorised"));
    }


    @After
    public void clean() {
        userClient.deleteUser(accessToken);
    }

}
