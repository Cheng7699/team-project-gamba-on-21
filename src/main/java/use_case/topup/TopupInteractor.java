package use_case.topup;

import entity.Accounts;
import entity.AccountFactory;

/**
 * Pre: User is logged into an account, and the username passed in already exists in data
 */


public class TopupInteractor implements TopupInputBoundary {
    private final TopupUserDataAccessInterface topupUserDataAccess;
    private final TopupOutputBoundary userpresenter;
    private final AccountFactory accountFactory;

    public TopupInteractor(TopupUserDataAccessInterface topupUserDataAccess,
                           TopupOutputBoundary topupOutputBoundary,
                           AccountFactory accountFactory) {
        this.topupUserDataAccess = topupUserDataAccess;
        this.userpresenter = topupOutputBoundary;
        this.accountFactory = accountFactory;
    }

    @Override
    public void execute(TopupInputData topupInputData) {
        try {
            int topUpAmount = Integer.parseInt(topupInputData.getTopupAmount());
            final Accounts user = topupUserDataAccess.get(topupInputData.getUsername());
            if (user == null) {
                userpresenter.prepareFailureView("User not found.");
                return;
            }
            user.addFunds(topUpAmount);
            topupUserDataAccess.topup(user);
            
            final TopupOutputData topupOutputData = new TopupOutputData(user.getUsername());
            userpresenter.prepareSuccessView(topupOutputData);
        }
        catch(NumberFormatException e) {
            userpresenter.prepareFailureView("Please enter an integer");
        }
    }
}
