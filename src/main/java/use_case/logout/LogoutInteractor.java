package use_case.logout;

/**
 * The Logout Interactor.
 */
public class LogoutInteractor implements LogoutInputBoundary {
    private LogoutUserDataAccessInterface userDataAccessObject;
    private LogoutOutputBoundary logoutPresenter;

    public LogoutInteractor(LogoutUserDataAccessInterface userDataAccessInterface,
                            LogoutOutputBoundary logoutOutputBoundary) {

        this.userDataAccessObject = userDataAccessInterface;
        this.logoutPresenter = logoutOutputBoundary;
    }

    @Override
    public void execute() {

        String username = userDataAccessObject.getCurrentUsername();
        // 1. set the current username to null in the DAO
        userDataAccessObject.setCurrentUsername(null);
        // 2. instantiate the `LogoutOutputData`, which needs to contain the username.
        LogoutOutputData logoutdata = new LogoutOutputData(username);
        // 3. tell the presenter to prepare a success view.
        logoutPresenter.prepareSuccessView(logoutdata);
    }
}

