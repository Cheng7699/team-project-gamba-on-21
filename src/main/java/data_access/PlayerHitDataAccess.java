package data_access;

import entity.Card;
import entity.BlackjackGame;
import use_case.playerHit.PlayerHitUserDataAccessInterface;
import java.io.IOException;

public class PlayerHitDataAccess implements PlayerHitUserDataAccessInterface {
    private DeckApiClient deck;
    private BlackjackGame game;

    public PlayerHitDataAccess(DeckApiClient deck, BlackjackGame game) {
        this.deck = deck;
        this.game = game;
    }

    @Override
    public Card drawCard() {
        try {
            return deck.drawCard(game.getDeckID());
        }  catch (IOException e) {
            throw new RuntimeException("Failed to draw a card.", e);
        }
    }
}
