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

    private LoggedInViewModel loggedInViewModel;
    private ViewManagerModel viewManagerModel;
    private LoginViewModel loginViewModel;

    public LogoutPresenter(ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel,
                           LoginViewModel loginViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(LogoutOutputData response) {
        // We need to switch to the login view, which should have
        // an empty username and password.

        // We also need to set the username in the LoggedInState to
        // the empty string.

        // Update LoggedInState

        // 1. get the LoggedInState out of the appropriate View Model
        LoggedInState loggedInState = loggedInViewModel.getState();
        // 2. set the username in the state to the empty string
        loggedInState.setUsername("");
        // 3. firePropertyChanged so that the View that is listening is updated.
        loggedInViewModel.setState(loggedInState);
        loggedInViewModel.firePropertyChange();

        // Update LoginState

        // 1. get the LoginState out of the appropriate View Model
        LoginState loginState = loginViewModel.getState();
        // 2. set the username in the state to be the username of the user that just logged out
        loginState.setUsername(response.getUsername());
        // 3. firePropertyChanged so that the View that is listening is updated.
        loginViewModel.setState(loginState);
        loginViewModel.firePropertyChange();

        // This code tells the View Manager to switch to the LoginView.
        this.viewManagerModel.setState(loginViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
