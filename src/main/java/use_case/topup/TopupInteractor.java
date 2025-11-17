package use_case.topup;

import entity.Accounts;
import entity.AccountFactory;
import use_case.login.LoginUserDataAccessInterface;


public class TopupInteractor implements TopupInputBoundary {
    private final TopupUserDataAccessInterface topupUserDataAccess;
    private final TopupOutputBoundary topupOutputBoundary;
    private final AccountFactory accountFactory;

    public TopupInteractor(TopupUserDataAccessInterface topupUserDataAccess,
                           TopupOutputBoundary topupOutputBoundary,
                           AccountFactory accountFactory) {
        this.topupUserDataAccess = topupUserDataAccess;
        this.topupOutputBoundary = topupOutputBoundary;
        this.accountFactory = accountFactory;
    }

    @Override
    public void execute(TopupInputData topupInputData) {
        //TODO: Finish the Interactor
    }
}
