package interface_adapter.launch;

import use_case.launch.LaunchInputBoundary;

public class LaunchController {

    private final LaunchInputBoundary launchUseCaseInteractor;

    public LaunchController(LaunchInputBoundary launchUseCaseInteractor) {
        this.launchUseCaseInteractor = launchUseCaseInteractor;
    }

    public void SwitchToSignUp() {
        launchUseCaseInteractor.switchToSignUpView();
    }

    public void SwitchToLogIn() {
        launchUseCaseInteractor.switchToLoginView();
    }

}
