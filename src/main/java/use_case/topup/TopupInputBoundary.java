package use_case.topup;

/**
 * The TopUp Use case
 */

public interface TopupInputBoundary {

    /**
     * Execute the TopUp Use Case
     * @param topupInputData the input data for this use case!
     */
    void execute(TopupInputData topupInputData);

}
