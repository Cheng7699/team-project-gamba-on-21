package interface_adapter.PlayerStand;

import entity.BlackjackGame;
import entity.BlackjackPlayer;
import entity.Hand;
import use_case.playerStand.PlayerStandOutputBoundary;
import use_case.playerStand.PlayerStandOutputData;
import view.BlackjackView;

public class PlayerStandPresenter implements PlayerStandOutputBoundary {

    private BlackjackView view;

    public PlayerStandPresenter(BlackjackView view) { this.view = view; }

    public void present(PlayerStandOutputData outputData) {

        BlackjackGame game = outputData.getGame();
        view.setHands(game.getPlayer().getHands().get(0), game.getDealer().getHand(), true);
        view.setGame(game);
        view.showDealerCard();

        Hand playerHand1 = game.getPlayer().getHands().get(0);
        if (game.isSplitted()) {
            Hand playerHand2 = game.getPlayer().getHands().get(1);
        }
        Hand  dealerHand = game.getDealer().getHand();
        if (game.getPlayer().getHands().get(0).isBust()
        || (game.isSplitted() && game.getPlayer().getHands().get(1).isBust())) {
            view.showRoundResult("You Busted!");
        }
        else if (game.getDealer().getHand().isBust()) {
            view.showRoundResult("You Won!");
        }
        else if (dealerHand.getHandTotalNumber() > playerHand1.getHandTotalNumber()) {view.showRoundResult("You Lost");}
        else if (dealerHand.getHandTotalNumber() == playerHand1.getHandTotalNumber()) {view.showRoundResult("It's a push!");}
        else {view.showRoundResult("You Won!");}
    }

    public void presentFailView(String message) {
        System.out.print(message);
    }
}
