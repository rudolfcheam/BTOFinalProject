package control;

import entity.User;
import utility.DataStore;

public class AuthController implements AuthService {

    public User login(String nric, String password) {
        for (User user : DataStore.getUsers()) {
            if (user.getNric().equalsIgnoreCase(nric) && user.getPassword().equals(password)) {
                System.out.printf("Successful user login for %s\n", nric);
                return user;
            }
        }
        return null;
    }
    public String changeUserPassword(User user, String oldPassword, String newPassword) {
        if (user.changePassword(oldPassword, newPassword)) {
            return "SUCCESS";
        } else if (!user.getPassword().equals(oldPassword)) {
            return "WRONG_OLD_PASSWORD";
        } else {
            return "INVALID_NEW_PASSWORD"; 
        }
    }
}
