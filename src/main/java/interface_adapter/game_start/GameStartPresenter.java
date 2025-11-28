package interface_adapter.game_start;

import entity.BlackjackGame;
import use_case.game_start.GameStartOutputBoundary;
import use_case.game_start.GameStartOutputData;
import view.BlackjackView;

public class GameStartPresenter implements GameStartOutputBoundary {

    private final BlackjackView view;

    public GameStartPresenter(BlackjackView view) { this.view = view; }

    public void present(GameStartOutputData outputData) {
        BlackjackGame game = outputData.getGame();
        view.setHands(game.getPlayer().getHands().get(0), game.getDealer().getHand(), true);
        view.setGame(game);
    }

    public void presentFailView(String message) {
        System.out.print(message);
    }
}
