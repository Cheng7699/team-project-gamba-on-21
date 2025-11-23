package use_case.launch;

public interface LaunchOutputBoundary {
    /**
     * Prepares the success view for the Launch Use Case
     * @param outputData the output data
     */
    void prepareSuccessView(LaunchOutputData outputData);

    /**
     * Prepares the failure view for the Launch Use Case
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
