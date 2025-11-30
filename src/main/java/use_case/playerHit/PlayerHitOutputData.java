package use_case.playerHit;

import entity.Hand;

public class PlayerHitOutputData {

    private final Hand handAfterHit;
    private final boolean splitHand;

    public PlayerHitOutputData(Hand handAfterHit, boolean splitHand) {
        this.handAfterHit = handAfterHit;
        this.splitHand = splitHand;
    }

    public Hand getHandAfterHit() { return handAfterHit; }
    public boolean isSplitHand() { return splitHand; }
}
