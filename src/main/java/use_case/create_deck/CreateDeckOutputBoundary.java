package use_case.create_deck;

/**
 * The output boundary for the Create Deck Use Case.
 */

public interface CreateDeckOutputBoundary {
    /**
     * Prepares the success view for the Create Deck Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(CreateDeckOutputData outputData);
    void prepareFailView(CreateDeckOutputData outputData);

}
