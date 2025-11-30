package data_access;

import entity.Accounts;
import entity.BlackjackGame;
import entity.Card;
import use_case.playerDoubleDown.PlayerDoubleDownUserDataAccessInterface;
import use_case.placeBet.PlaceBetUserDataAccessInterface;

import java.io.IOException;

/**
 * Data access implementation for Player Double Down use case.
 * Combines account access (for balance operations) and deck access (for drawing cards).
 */
public class PlayerDoubleDownDataAccess implements PlayerDoubleDownUserDataAccessInterface {
    private final PlaceBetUserDataAccessInterface accountAccess;
    private final DeckApiClient deck;
    private final BlackjackGame game;

    public PlayerDoubleDownDataAccess(PlaceBetUserDataAccessInterface accountAccess,
                                      DeckApiClient deck,
                                      BlackjackGame game) {
        this.accountAccess = accountAccess;
        this.deck = deck;
        this.game = game;
    }

    @Override
    public Accounts get(String username) {
        return accountAccess.get(username);
    }

    @Override
    public void save(Accounts account) {
        accountAccess.save(account);
    }

    @Override
    public String getCurrentUsername() {
        return accountAccess.getCurrentUsername();
    }

    @Override
    public Card drawCard() {
        try {
            return deck.drawCard(game.getDeckID());
        } catch (IOException e) {
            throw new RuntimeException("Failed to draw a card for double down.", e);
        }
    }
}

