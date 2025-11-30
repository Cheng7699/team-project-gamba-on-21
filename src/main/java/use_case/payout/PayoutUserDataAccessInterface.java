package use_case.payout;

import entity.Accounts;

/**
 * Data access interface for payout use case.
 */
public interface PayoutUserDataAccessInterface {
    /**
     * Gets the account for the given username.
     * @param username the username to look up
     * @return the account
     */
    Accounts get(String username);

    /**
     * Saves the updated account balance.
     * @param account the account with updated balance
     */
    void save(Accounts account);

    /**
     * Gets the current logged-in username.
     * @return the current username
     */
    String getCurrentUsername();
}

