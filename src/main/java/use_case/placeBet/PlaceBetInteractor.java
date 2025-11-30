package use_case.placeBet;

import entity.Accounts;

/**
 * Interactor for placing a bet and deducting it from balance.
 */
public class PlaceBetInteractor {
    private final PlaceBetUserDataAccessInterface userDataAccessObject;
    private final PlaceBetOutputBoundary presenter;

    public PlaceBetInteractor(PlaceBetUserDataAccessInterface userDataAccessObject,
                             PlaceBetOutputBoundary presenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.presenter = presenter;
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

        // notify presenter to update view model
        PlaceBetOutputData outputData = new PlaceBetOutputData(account.getBalance());
        presenter.prepareSuccessView(outputData);
    }
}

