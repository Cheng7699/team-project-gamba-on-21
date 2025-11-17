package use_case.deck_draw;

/**
 * The output boundary for the Deck Draw Use Case.
 */

public interface DeckDrawOutputBoundary {
    /**
     * Prepares the success view for the Create Deck Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(DeckDrawOutputData outputData);
    void prepareFailureView(DeckDrawOutputData outputData);
}
