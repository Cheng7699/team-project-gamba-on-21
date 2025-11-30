package interface_adapter.placeBet;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.placeBet.PlaceBetOutputBoundary;
import use_case.placeBet.PlaceBetOutputData;

/**
 * Presenter for place bet use case.
 */
public class PlaceBetPresenter implements PlaceBetOutputBoundary {
    private final LoggedInViewModel loggedInViewModel;

    public PlaceBetPresenter(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(PlaceBetOutputData outputData) {
        // update balance in logged in state
        LoggedInState state = loggedInViewModel.getState();
        state.setBalance(outputData.getNewBalance());
        loggedInViewModel.setState(state);
        // fire both "state" and "balance" property changes to ensure UI updates
        loggedInViewModel.firePropertyChange();
        loggedInViewModel.firePropertyChange("balance");
    }
}

