package use_case.deck_draw;

public class DeckDrawOutputData {

    private final String[] cardArray;

    public DeckDrawOutputData(String[] cardArray) {
        this.cardArray = cardArray;
    }
    public String[] getCardArray() { return cardArray; }
}
