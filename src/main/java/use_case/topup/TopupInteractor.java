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
            int topupAmount = Integer.parseInt(topupInputData.getTopupAmount());
            final Accounts user = accountFactory.create()       //TODO: add userfactory functions!
        }
        catch(NumberFormatException e)
        {userpresenter.prepareFailureView("Please enter an integer");}
    }
}
