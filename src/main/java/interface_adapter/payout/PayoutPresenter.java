package interface_adapter.payout;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.payout.PayoutOutputBoundary;
import use_case.payout.PayoutOutputData;

/**
 * Presenter for payout use case.
 */
public class PayoutPresenter implements PayoutOutputBoundary {
    private final LoggedInViewModel loggedInViewModel;

    public PayoutPresenter(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(PayoutOutputData outputData) {
        // update balance in logged in state
        LoggedInState state = loggedInViewModel.getState();
        state.setBalance(outputData.getNewBalance());
        loggedInViewModel.setState(state);
        loggedInViewModel.firePropertyChange("balance");
    }
}

