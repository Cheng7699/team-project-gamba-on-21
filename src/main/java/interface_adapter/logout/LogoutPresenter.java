package interface_adapter.logout;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import use_case.logout.LogoutOutputBoundary;
import use_case.logout.LogoutOutputData;

/**
 * The Presenter for the Logout Use Case.
 */
public class LogoutPresenter implements LogoutOutputBoundary {
    // REFACTORED: making fields final for immutability and thread safety
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoginViewModel loginViewModel;

    public LogoutPresenter(ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel,
                           LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(LogoutOutputData response) {
        // REFACTORED: extracting state update logic into separate methods for SRP
        clearLoggedInState();
        updateLoginState(response.getUsername());
        switchToLoginView();
    }

    // REFACTORED: extracting method to clear logged-in state (SRP)
    private void clearLoggedInState() {
        LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setUsername("");
        loggedInViewModel.setState(loggedInState);
        loggedInViewModel.firePropertyChange();
    }

    // REFACTORED: extracting method to update login state (srp)
    private void updateLoginState(String username) {
        LoginState loginState = loginViewModel.getState();
        loginState.setUsername(username);
        loginState.setPassword("");
        loginState.setLoginError(null);
        loginViewModel.setState(loginState);
        loginViewModel.firePropertyChange();
    }

    // REFACTORED: extracting method to switch views (SRP)
    private void switchToLoginView() {
        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
