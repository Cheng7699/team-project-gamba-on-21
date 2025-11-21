package use_case.playerStand;

import entity.BlackjackGame;
import entity.BlackjackPlayer;

public class PlayerStandInputData {
    private BlackjackPlayer blackjackPlayer;
    private BlackjackGame blackjackGame;
    private boolean isInSplittedHand;

    public PlayerStandInputData(BlackjackPlayer blackjackPlayer, BlackjackGame blackjackGame, boolean isInSplittedHand) {
        this.blackjackPlayer = blackjackPlayer;
        this.blackjackGame = blackjackGame;
        this.isInSplittedHand = isInSplittedHand;
    }

    public BlackjackPlayer getBlackjackPlayer() { return blackjackPlayer; }
    public BlackjackGame getBlackjackGame() { return blackjackGame; }
    public boolean isInSplittedHand() { return isInSplittedHand; }
}

