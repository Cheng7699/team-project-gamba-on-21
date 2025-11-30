package interface_adapter.playerSplit;

import entity.BlackjackGame;
import entity.Hand;
import use_case.playerSplit.PlayerSplitOutputBoundary;
import use_case.playerSplit.PlayerSplitOutputData;
import view.BlackjackView;

public class PlayerSplitPresenter implements PlayerSplitOutputBoundary {

    private final BlackjackView view;

    public PlayerSplitPresenter(BlackjackView view) {
        this.view = view;
    }

    @Override
    public void present(PlayerSplitOutputData outputData) {
        BlackjackGame game = outputData.getGame();
        Hand primaryHand = game.getPlayer().getHands().get(0);
        Hand splitHand = game.getPlayer().getHands().get(1);

        view.setHands(primaryHand, splitHand, game.getDealer().getHand(), true);
        view.setGame(game);
        view.showStatusMessage("Hand split! Play your first hand, then stand to move to the second.");
    }

    @Override
    public void presentFailView(String message) {
        view.showStatusMessage(message);
    }
}
