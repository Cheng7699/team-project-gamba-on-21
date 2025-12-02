package use_case.topup;

import entity.Accounts;

/**
 * Pre: User is logged into an account, and the username passed in already exists in data.
 */

public class TopupInteractor implements TopupInputBoundary {
    private final TopupUserDataAccessInterface topupUserDataAccess;
    private final TopupOutputBoundary userpresenter;

    public TopupInteractor(TopupUserDataAccessInterface topupUserDataAccess,
                           TopupOutputBoundary topupOutputBoundary) {
        this.topupUserDataAccess = topupUserDataAccess;
        this.userpresenter = topupOutputBoundary;
    }

    @Override
    public void execute(TopupInputData topupInputData) {
        try {
            final int topUpAmount = Integer.parseInt(topupInputData.getTopupAmount());

            if (topUpAmount <= 0) {
                userpresenter.prepareFailureView("Amount must be greater than 0");
                return;
            }

            final Accounts user = topupUserDataAccess.get(topupInputData.getUsername());
            if (user == null) {
                userpresenter.prepareFailureView("User not found.");
                return;
            }
            user.addFunds(topUpAmount);
            topupUserDataAccess.topup(user);
            final TopupOutputData topupOutputData = new TopupOutputData(user.getBalance());

            userpresenter.prepareSuccessView(topupOutputData);
        }
        catch (NumberFormatException e) {
            userpresenter.prepareFailureView("Please enter an integer");
        }
    }
}
