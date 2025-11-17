package use_case.deck_draw;

/**
 * The input data for the Deck Draw Use Case.
 */

public class DeckDrawInputData {
    private final String deckID;
    private final Integer drawNumber; // The number of cards drawn

    public DeckDrawInputData(String deckID, Integer drawNumber) {
        this.deckID = deckID;
        this.drawNumber = drawNumber;
    }

    public String getDeckID() { return deckID; }
    public Integer getDrawNumber() { return drawNumber; }
}
