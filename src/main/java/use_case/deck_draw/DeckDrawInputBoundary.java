package use_case.deck_draw;

/**
 * The Deck Draw Use Case.
 */

public interface DeckDrawInputBoundary {

    /**
     * Execute the Deck Draw Use Case.
     * @param deckDrawInputData the input data for this use case
     */
    void execute(DeckDrawInputData deckDrawInputData);
}
