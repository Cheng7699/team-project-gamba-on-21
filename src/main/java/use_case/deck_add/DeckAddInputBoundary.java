package use_case.deck_add;

/**
 * The Deck Add Use Case.
 */

public interface DeckAddInputBoundary {

    /**
     * Execute the Deck Add Use Case.
     * @param deckAddInputData the input data for this use case
     */
    void execute(DeckAddInputData deckAddInputData);
}
