package use_case.create_deck;

/**
 * The Create Deck Use Case.
 */

public interface CreateDeckInputBoundary {

    /**
     * Execute the Create Deck Use Case.
     * @param createDeckInputData the input data for this use case
     */
    void execute(CreateDeckInputData createDeckInputData);
}
