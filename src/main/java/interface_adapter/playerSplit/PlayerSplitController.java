package interface_adapter.playerSplit;

import entity.BlackjackGame;
import use_case.playerSplit.PlayerSplitInputBoundary;
import use_case.playerSplit.PlayerSplitInputData;

public class PlayerSplitController {

    private final PlayerSplitInputBoundary interactor;

    public PlayerSplitController(PlayerSplitInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void split(BlackjackGame game) {
        PlayerSplitInputData inputData = new PlayerSplitInputData(game);
        interactor.execute(inputData);
    }
}
