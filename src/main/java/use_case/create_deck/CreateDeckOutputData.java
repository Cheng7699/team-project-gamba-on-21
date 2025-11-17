package use_case.create_deck;

public class CreateDeckOutputData {

    private final String deckID;
    private final Boolean shuffled;
    private final Integer remainingCards;

    public CreateDeckOutputData(String deckID,  Boolean shuffled, Integer remainingCards) {
        this.deckID = deckID;
        this.shuffled =  shuffled;
        this.remainingCards =  remainingCards;
        }

    public String getDeckID() { return this.deckID; }
    public Boolean getShuffled() { return this.shuffled; }
    public Integer getRemainingCards() { return this.remainingCards; }
}
