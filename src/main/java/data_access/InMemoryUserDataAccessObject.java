package data_access;

import entity.Accounts;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of the DAO for storing user data. This implementation does
 * NOT persist data between runs of the program.
 */
public class InMemoryUserDataAccessObject implements SignupUserDataAccessInterface,
                                                     LoginUserDataAccessInterface,
                                                     ChangePasswordUserDataAccessInterface,
                                                     LogoutUserDataAccessInterface {

    private final Map<String, Accounts> users = new HashMap<>();

    private String currentUsername;

    @Override
    public boolean existsByName(String identifier) {
        return users.containsKey(identifier);
    }

    @Override
    public void save(Accounts user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public Accounts get(String username) {
        return users.get(username);
    }

    @Override
    public void setCurrentUsername(String name) {
        currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void changePassword(Accounts user) {
        // Replace the old entry with the new password
        users.put(user.getUsername(), user);
    }

}
