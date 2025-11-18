package use_case.topup;

import entity.Accounts;

/**
 * The DAO interface for the TopUp use case
 */
public interface TopupUserDataAccessInterface {

    /**
     * Update the system to record this user's balance
     * @param user the user whose balance is to be updated
     */
   void topup(Accounts user);

   Accounts get(String name);
}