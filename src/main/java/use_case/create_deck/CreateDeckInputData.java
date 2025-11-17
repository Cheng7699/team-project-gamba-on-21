package use_case.create_deck;

/**
 * The input data for the Create Deck Use Case.
 */

public class CreateDeckInputData {

    private final Boolean jokersEnabled; // A boolean for whether to include jokers in the deck
    private final Integer deckNumber; // The number of decks to initialize
    private final Boolean shuffled;


    public CreateDeckInputData(Boolean jokersEnabled, Integer deckNumber,  Boolean shuffled) {
        this.jokersEnabled = jokersEnabled;
        this.deckNumber = deckNumber;
        this.shuffled = shuffled;

    }

    public Boolean getJokersEnabled() { return jokersEnabled; }
    public Integer getDeckNumber() { return deckNumber; }
    public Boolean getShuffled() { return shuffled; }
}

