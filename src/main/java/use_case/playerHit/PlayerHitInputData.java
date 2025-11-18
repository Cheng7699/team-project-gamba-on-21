package use_case.playerHit;

import entity.BlackjackPlayer;

public class PlayerHitInputData {
    private BlackjackPlayer blackjackPlayer;
    private boolean isInSplittedHand;

    public PlayerHitInputData(BlackjackPlayer blackjackPlayer, boolean isInSplittedHand) {
        this.blackjackPlayer = blackjackPlayer;
        this.isInSplittedHand = isInSplittedHand;
    }

    public BlackjackPlayer getBlackjackPlayer() { return blackjackPlayer; }
    public boolean isInSplittedHand() { return isInSplittedHand; }
}
