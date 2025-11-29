package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;

/**
 * The Presenter for the Login Use Case.
 */
public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel,
                          LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(LoginOutputData response) {
        // REFACTORED: extracting state update logic into separate methods for SRP
        updateLoggedInState(response);
        clearLoginState();
        switchToLoggedInView();
    }

    // REFACTORED: extracting method to update logged-in state (SRP)
    private void updateLoggedInState(LoginOutputData response) {
        LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setUsername(response.getUsername());
        loggedInState.setBalance(response.getBalance());
        loggedInState.setSelfLimit(response.getSelfLimit());
        loggedInState.setStatusMessage("Welcome back, " + response.getUsername() + "!");
        loggedInViewModel.firePropertyChange();
        loggedInViewModel.firePropertyChange("balance");
        loggedInViewModel.firePropertyChange("message");
    }

    // REFACTORED: extracting method to clear login state (SRP)
    private void clearLoginState() {
        loginViewModel.setState(new LoginState());
    }

    // REFACTORED: extracting method to switch views (SRP)
    private void switchToLoggedInView() {
        viewManagerModel.setState(loggedInViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final LoginState loginState = loginViewModel.getState();
        loginState.setLoginError(error);
        loginViewModel.firePropertyChange();
    }
}
