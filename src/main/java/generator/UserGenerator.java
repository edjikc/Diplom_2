package generator;

import constant.UserConstants;
import pojo.User;

public class UserGenerator {

    public static User createUser(){
        return createUserByFields(UserConstants.EMAIL, UserConstants.PASS, UserConstants.NAME);
    }

    public static User createUserByFields(String email, String password, String name) {
        return new User(email, password, name);
    }
    public static User createUserByEmail(String email){
        return new User(email, "pass", "TestName");
    }

    public static User createInvalidUser(){
        return new User(null, "pass", "TestName");
    }
}
