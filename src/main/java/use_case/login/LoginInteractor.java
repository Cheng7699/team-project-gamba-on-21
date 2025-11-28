package use_case.login;

import entity.Accounts;

/**
 * The Login Interactor.
 */
public class LoginInteractor implements LoginInputBoundary {
    private final LoginUserDataAccessInterface userDataAccessObject;
    private final LoginOutputBoundary loginPresenter;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccessInterface,
                           LoginOutputBoundary loginOutputBoundary) {
        this.userDataAccessObject = userDataAccessInterface;
        this.loginPresenter = loginOutputBoundary;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();
        
        // REFACTORED: extracting validation logic for better separation of concerns
        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView(username + ": Account does not exist.");
            return;
        }
        
        // REFACTORED: extracting user retrieval to avoid duplicate calls
        final Accounts user = userDataAccessObject.get(username);
        
        // REFACTORED: extracting password validation for single responsibility
        if (!isPasswordValid(password, user.getPassword())) {
            loginPresenter.prepareFailView("Incorrect password for \"" + username + "\".");
            return;
        }
        
        // REFACTORED: extracting success logic for better readability
        performSuccessfulLogin(username, user);
    }

    // REFACTORED: extracting password validation method (single responsibility principle)
    private boolean isPasswordValid(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);
    }

    // REFACTORED: extracting successful login logic (single responsibility principle)
    private void performSuccessfulLogin(String username, Accounts user) {
        userDataAccessObject.setCurrentUsername(username);
        LoginOutputData loginOutputData = new LoginOutputData(
                user.getUsername(),
                user.getBalance(),
                user.getSelfLimits()
        );
        loginPresenter.prepareSuccessView(loginOutputData);
    }
}
