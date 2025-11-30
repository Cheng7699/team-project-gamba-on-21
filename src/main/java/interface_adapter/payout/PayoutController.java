package interface_adapter.payout;

import use_case.payout.PayoutInputData;
import use_case.payout.PayoutInteractor;

/**
 * Controller for payout use case.
 */
public class PayoutController {
    private final PayoutInteractor payoutInteractor;

    public PayoutController(PayoutInteractor payoutInteractor) {
        this.payoutInteractor = payoutInteractor;
    }

    public void execute(PayoutInputData inputData) {
        payoutInteractor.execute(inputData);
    }
}

