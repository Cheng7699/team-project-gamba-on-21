package use_case.playerHit;

import entity.BlackjackPlayer;

public class PlayerHitInputData {
    private BlackjackPlayer blackjackPlayer;

    public PlayerHitInputData(BlackjackPlayer blackjackPlayer) {
        this.blackjackPlayer = blackjackPlayer;
    }

    public BlackjackPlayer getBlackjackPlayer() { return blackjackPlayer; }
}
