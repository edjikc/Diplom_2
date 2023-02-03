package generator;

import pojo.Credentials;
import pojo.User;

public class CredentialsGenerator {
    public static Credentials generateCredentialsFromUser(User user){
        return generateFromEmailAndPass(user.getEmail(), user.getPassword());
    }

    public static Credentials generateFromEmailAndPass(String email, String pass){
        return new Credentials(email, pass);
    }
}
