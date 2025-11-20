package use_case.playerHit;

import entity.Hand;

public class PlayerHitOutputData {

    private final Hand handAfterHit;

    public PlayerHitOutputData(Hand handAfterHit) {
        this.handAfterHit = handAfterHit;
    }

    public Hand getHandAfterHit() { return handAfterHit; }
}
