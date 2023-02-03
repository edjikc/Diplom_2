package generator;

import pojo.User;

public class UserGenerator {

    public static User createUser(){
        return new User("123@123.ru", "pass", "TestName");
    }
    public static User createUserByEmail(String email){
        return new User(email, "pass", "TestName");
    }

    public static User createInvalidUser(){
        return new User(null, "pass", "TestName");
    }
}
