import client.UserClient;
import generator.UserGenerator;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class CreateUserTest {

    private UserClient client;
    private User user;
    private String accessToken = "";


    @Before
    public void setup() {
        client = new UserClient();
        user = UserGenerator.createUser();
        client.deleteUserIfExist(user);
    }

    @Test
    public void createNewUser() {
        ValidatableResponse userResponse = client.createNewUser(user).then();
        userResponse.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));
        accessToken = userResponse.extract().path("accessToken");
    }

    @Test
    public void createExistedUser() {
        createNewUser();
        ValidatableResponse userResponse = client.createNewUser(user).then();
        userResponse.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("User already exists"));
    }

    @Test
    public void createInvalidUser(){
        User invalidUser = UserGenerator.createInvalidUser();
        ValidatableResponse then = client.createNewUser(invalidUser).then();
        then.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @After
    public void clean() {
        client.deleteUser(accessToken);
    }
}
