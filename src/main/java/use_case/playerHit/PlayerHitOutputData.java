package use_case.playerHit;

import entity.Hand;

public class PlayerHitOutputData {

    private final Hand handAfterHit;
    private final boolean isBust;

    public PlayerHitOutputData(Hand handAfterHit, boolean isBust) {

        this.handAfterHit = handAfterHit;
        this.isBust = isBust;
    }

    public Hand getHandAfterHit() {
        return handAfterHit;
    }

    public boolean isBust() {
        return isBust;
    }
}
