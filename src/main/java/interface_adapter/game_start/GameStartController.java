package interface_adapter.game_start;

import entity.BlackjackGame;
import use_case.game_start.GameStartInputBoundary;
import use_case.game_start.GameStartInputData;

public class GameStartController {

    private final GameStartInputBoundary interactor;

    public GameStartController(GameStartInputBoundary interactor) { this.interactor = interactor; }

    public void gameStart(BlackjackGame game, Integer betAmount) {
        interactor.execute(new GameStartInputData(game, betAmount));
    }
}
