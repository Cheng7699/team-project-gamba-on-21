package use_case.payout;

/**
 * Output boundary for payout use case.
 */
public interface PayoutOutputBoundary {
    /**
     * Prepares the success view with payout information.
     * @param outputData the payout output data
     */
    void prepareSuccessView(PayoutOutputData outputData);
}

