package use_case.deck_add;

/**
 * The input data for the Deck Draw Use Case.
 */

public class DeckAddInputData {
    private final String deckID;
    private final String[] cardArray; // The number of cards drawn

    public DeckAddInputData(String deckID, String[] cardArray) {
        this.deckID = deckID;
        this.cardArray = cardArray;
    }

    public String getDeckID() { return deckID; }
    public String[] getCardArray() { return cardArray; }
    public Integer cardNumber() { return cardArray.length; }
}
