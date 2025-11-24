package use_case.playerStand;

import entity.BlackjackGame;
import entity.Hand;

public class PlayerStandOutputData {
    private final BlackjackGame blackjackGame;
    private final Hand dealerHand;
    private final Hand playerHand;

    public PlayerStandOutputData(BlackjackGame blackjackGame, Hand dealerHand, Hand playerHand) {
        this.blackjackGame = blackjackGame;
        this.dealerHand = dealerHand;
        this.playerHand = playerHand;
    }

    public BlackjackGame getBlackjackGame() { return blackjackGame; }
    public Hand getDealerHand() { return dealerHand; }
    public Hand getPlayerHand() { return playerHand; }
}
