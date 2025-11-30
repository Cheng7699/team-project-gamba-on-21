package use_case.playerHit;

import entity.Hand;

public class PlayerHitOutputData {

    private final Hand handAfterHit;
    private final boolean splitHand;
    private final boolean isBust;

    public PlayerHitOutputData(Hand handAfterHit, boolean splitHand, boolean isBust) {
        this.handAfterHit = handAfterHit;
        this.splitHand = splitHand;
        this.isBust = isBust;
    }

    public Hand getHandAfterHit() { return handAfterHit; }
    public boolean isSplitHand() { return splitHand; }
    public boolean isBust() { return isBust; }
}
