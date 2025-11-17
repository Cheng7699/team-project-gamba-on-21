package use_case.change_password;

import entity.Accounts;

/**
 * The DAO interface for the Change Password Use Case.
 */
public interface ChangePasswordUserDataAccessInterface {

    /**
     * Updates the system to record this user's password.
     * @param account the user whose password is to be updated
     */
    void changePassword(Accounts account);
}
