package use_case.deck_add;

public class DeckAddOutputData {

    private final String[] cardArray;

    public DeckAddOutputData(String[] cardArray) {
        this.cardArray = cardArray;
    }
    public String[] getCardArray() { return cardArray; }
}
