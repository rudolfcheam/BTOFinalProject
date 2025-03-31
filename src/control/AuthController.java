package control;

import entity.User;
import utility.DataStore;

public class AuthController {

    public User login(String nric, String password) {
        for (User user : DataStore.getUsers()) {
            if (user.getNric().equalsIgnoreCase(nric) && user.getPassword().equals(password)) {
                System.out.printf("Successful user login for %s\n", nric);
                return user;
            }
        }
        return null;
    }
}
