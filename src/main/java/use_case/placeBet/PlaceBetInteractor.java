package use_case.placeBet;

import entity.Accounts;

/**
 * Interactor for placing a bet and deducting it from balance.
 */
public class PlaceBetInteractor {
    private final PlaceBetUserDataAccessInterface userDataAccessObject;

    public PlaceBetInteractor(PlaceBetUserDataAccessInterface userDataAccessObject) {
        this.userDataAccessObject = userDataAccessObject;
    }

    public void execute(int betAmount) {
        String username = userDataAccessObject.getCurrentUsername();
        Accounts account = userDataAccessObject.get(username);
        
        if (account == null) {
            return;
        }

        // deduct bet from balance
        account.subtractFunds(betAmount);
        userDataAccessObject.save(account);
    }
}

