package interface_adapter.launch;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupViewModel;
import use_case.launch.LaunchOutputBoundary;
import view.LaunchView;

public class LaunchPresenter implements LaunchOutputBoundary {
    private final ViewManagerModel viewManagerModel;
    private final LoginViewModel loginViewModel;
    private final SignupViewModel signupViewModel;
    private final LaunchViewModel launchViewModel;

    public LaunchPresenter(ViewManagerModel viewManagerModel,
                           LoginViewModel loginViewModel,
                           SignupViewModel signupViewModel,
                           LaunchViewModel launchViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loginViewModel = loginViewModel;
        this.signupViewModel = signupViewModel;
        this.launchViewModel = launchViewModel;
    }

    public void switchToSignUpView() {
        viewManagerModel.setState(signupViewModel.getViewName());
        viewManagerModel.firePropertyChange();
        launchViewModel.setState(new LaunchState());
    }

    public void switchToLoginView() {
        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChange();
        launchViewModel.setState(new LaunchState());
    }
}
