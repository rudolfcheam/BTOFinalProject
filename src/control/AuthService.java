package control;

import entity.User;

public interface AuthService {
    User login(String nric, String password);
    String changeUserPassword(User user, String oldPassword, String newPassword);
}
