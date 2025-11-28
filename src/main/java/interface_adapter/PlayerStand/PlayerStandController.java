package interface_adapter.PlayerStand;

import use_case.playerStand.PlayerStandInputBoundary;
import use_case.playerStand.PlayerStandInputData;
import entity.BlackjackGame;
import entity.BlackjackPlayer;

public class PlayerStandController {

    private final PlayerStandInputBoundary interactor;

    public PlayerStandController(PlayerStandInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void stand(BlackjackGame game) {
        interactor.execute(new PlayerStandInputData(game));
    }
}
