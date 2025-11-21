package use_case.deck_add;

/**
 * The output boundary for the Deck Draw Use Case.
 */

public interface DeckAddOutputBoundary {
    /**
     * Prepares the success view for the Create Deck Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(DeckAddOutputData outputData);
    void prepareFailureView(DeckAddOutputData outputData);
}
