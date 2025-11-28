package use_case.game_start;

import entity.Card;

import java.io.IOException;

public interface GameStartDataAccessInterface {

    public String createDeck(Boolean shuffled, Boolean jokers) throws IOException;

    public Card drawCard(String deckId) throws IOException;

    public Card[] drawCards(String deckId, Integer number) throws IOException;

    public void addCard(String deckId, String pileName, Card card) throws IOException;

    public void addCards(String deckId, String pileName, Card[] cards) throws IOException;

    public void shuffle(String deckId) throws IOException;

}