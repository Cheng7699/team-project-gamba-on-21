package interface_adapter.launch;

import use_case.launch.LaunchInputBoundary;
import use_case.launch.LaunchInputData

public class LaunchController {

    private final LaunchInputBoundary launchUseCaseInteractor;

    public LaunchController(LaunchInputBoundary launchUseCaseInteractor) {
        this.launchUseCaseInteractor = launchUseCaseInteractor;
    }

    public void execute(String destinationPage) {
        final LaunchInputData launchInputData = new LaunchInputData(destinationPage);
        launchUseCaseInteractor.execute(launchInputData);
    }

    public void launchLogin() {
        launchPresenter.goToLogin();
    }
}
