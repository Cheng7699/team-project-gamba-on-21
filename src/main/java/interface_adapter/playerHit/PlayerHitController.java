package interface_adapter.playerHit;

import use_case.playerHit.PlayerHitInputBoundary;
import use_case.playerHit.PlayerHitInputData;
import entity.BlackjackPlayer;

public class PlayerHitController {

    private final PlayerHitInputBoundary interactor;

    public PlayerHitController(PlayerHitInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void hit(BlackjackPlayer player, boolean isInSplittedHand) {
        interactor.execute(new PlayerHitInputData(player, isInSplittedHand));
    }
}