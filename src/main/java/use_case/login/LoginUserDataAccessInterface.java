package use_case.login;

import entity.Accounts;

/**
 * DAO interface for the Login Use Case.
 */
public interface LoginUserDataAccessInterface {

    /**
     * Checks if the given username exists.
     * @param username the username to look for
     * @return true if a user with the given username exists; false otherwise
     */
    boolean existsByName(String username);

    /**
     * Saves the user.
     * @param account the user to save
     */
    void save(Accounts account);

    /**
     * Returns the user with the given username.
     * @param username the username to look up
     * @return the user with the given username
     */
    Accounts get(String username);

    void setCurrentUsername(String name);

    String getCurrentUsername();
}
