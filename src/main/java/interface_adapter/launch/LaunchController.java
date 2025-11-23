package interface_adapter.launch;

import use_case.launch.LaunchInputBoundary;
import use_case.launch.LaunchInputData;

public class LaunchController {

    private final LaunchInputBoundary launchUseCaseInteractor;

    public LaunchController(LaunchInputBoundary launchUseCaseInteractor) {
        this.launchUseCaseInteractor = launchUseCaseInteractor;
    }

    public void SwitchToSignUp() {
        launchUseCaseInteractor.switchToSignUp();
    }

    public void SwitchToLogIn() {
        launchUseCaseInteractor.switchToLogIn();
    }

}
