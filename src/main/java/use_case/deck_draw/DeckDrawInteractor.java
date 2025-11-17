package use_case.deck_draw;

public class DeckDrawInteractor implements DeckDrawInputBoundary {
    private String deckID;
    private Integer drawNumber;

    public DeckDrawInteractor(String deckID, Integer drawNumber) {
        this.deckID = deckID;
        this.drawNumber = drawNumber;
    }

    @Override
    public void execute(DeckDrawInputData deckDrawInputData) {

    }
}
