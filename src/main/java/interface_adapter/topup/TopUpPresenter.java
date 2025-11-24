package interface_adapter.topup;


import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.signup.SignupState;
import use_case.topup.TopupOutputBoundary;
import use_case.topup.TopupOutputData;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.topup.TopUpState;
import view.LoggedInView;

public class TopUpPresenter implements TopupOutputBoundary {

    private final TopupViewModel topupViewModel;
    private final LoggedInViewModel loggedinViewModel;
    private final ViewManagerModel viewManagerModel;

    public TopUpPresenter(TopupViewModel topupViewModel,
                          LoggedInViewModel loggedinViewModel,
                          ViewManagerModel viewManagerModel) {
        this.topupViewModel = topupViewModel;
        this.loggedinViewModel = loggedinViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(TopupOutputData outputData) {
        // On success, go back to the logged in page
        final LoggedInState loggedinState = loggedinViewModel.getState();
        loggedinState.setUsername(outputData.getUsername());
        loggedinViewModel.firePropertyChange();

        viewManagerModel.setState(loggedinViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        final TopUpState topupState = topupViewModel.getState();
        //TODO: Finish Topup State
    }

}

