package interface_adapter.topup;

import use_case.topup.TopupInputBoundary;
import use_case.topup.TopupInputData;

/**
 * Controller for the Signup Use Case
 */



public class TopUpController {

    private final TopupInputBoundary userTopupUseCaseInteractor;

    public TopUpController(TopupInputBoundary userTopupUseCaseInteractor) {
        this.userTopupUseCaseInteractor = userTopupUseCaseInteractor;
    }

    /**
     * Executes the Topup Use Case.
     * @param username the username of the account to add to
     * @param topupAmount the amount to be topped up
     */
    public void execute(String username, String topupAmount) {
        final TopupInputData topupInputData = new TopupInputData(
                username, topupAmount);

        userTopupUseCaseInteractor.execute(topupInputData);
    }

}
