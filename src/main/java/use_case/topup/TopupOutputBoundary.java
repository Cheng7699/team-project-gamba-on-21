package use_case.topup;

/**
 * The output boundary for the Change Password Use Case
 */

public interface TopupOutputBoundary {
    /**
     * Prepares the success view for the Topup Use case.
     * @param outputData the output data
     */
    void prepareSuccessView(TopupOutputData outputData);

    /**
     * Prepares the failure view for the Change Password Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailureView(String errorMessage);



}
