package use_case.launch;

import use_case.login.LoginOutputBoundary;

public class LaunchInteractor implements LaunchInputBoundary{
    private final LoginOutputBoundary launchPresenter;

    public LaunchInteractor(LoginOutputBoundary launchPresenter){ this.launchPresenter = launchPresenter;}

    @Override
    public void switchToLogIn() { launchPresenter.switchToLogin(); }

    @Override
    public void switchToSignUp() { launchPresenter.switchToSignUp(); }
}
