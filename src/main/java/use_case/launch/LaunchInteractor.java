package use_case.launch;

import use_case.login.LoginOutputBoundary;

public class LaunchInteractor implements LaunchInputBoundary{
    private final LaunchOutputBoundary launchPresenter;

    public LaunchInteractor(LaunchOutputBoundary launchPresenter){ this.launchPresenter = launchPresenter;}

    @Override
    public void switchToLoginView() { launchPresenter.switchToLoginView(); }

    @Override
    public void switchToSignUpView() { launchPresenter.switchToSignUpView(); }
}
