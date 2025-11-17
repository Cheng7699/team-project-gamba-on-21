package use_case.create_deck;

public class CreateDeckInteractor implements CreateDeckInputBoundary{
    private String deckID;
    private Boolean shuffled;
    private Integer remainingCards;
    public CreateDeckInteractor(String deckID, Boolean shuffled, Integer remainingCards) {
        this.deckID = deckID;
        this.shuffled = shuffled;
        this.remainingCards = remainingCards;
    }

    @Override
    public void execute(CreateDeckInputData createDeckInputData) {

    }
}
