package interface_adapter.PlayerStand;

import entity.BlackjackGame;
import entity.Hand;
import interface_adapter.payout.PayoutController;
import use_case.playerStand.PlayerStandOutputBoundary;
import use_case.playerStand.PlayerStandOutputData;
import use_case.payout.PayoutInputData;
import view.BlackjackView;

public class PlayerStandPresenter implements PlayerStandOutputBoundary {

    private BlackjackView view;
    private PayoutController payoutController;

    public PlayerStandPresenter(BlackjackView view) { 
        this.view = view; 
    }

    public void setPayoutController(PayoutController payoutController) {
        this.payoutController = payoutController;
    }

    public void present(PlayerStandOutputData outputData) {

        BlackjackGame game = outputData.getGame();
        view.setHands(game.getPlayer().getHands().get(0), game.getDealer().getHand(), true);
        view.setGame(game);
        view.showDealerCard();

        Hand playerHand1 = game.getPlayer().getHands().get(0);
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

        // process payout after game ends
        if (payoutController != null && game.getState().equals("GameOver")) {
            PayoutInputData payoutInputData = new PayoutInputData(game);
            payoutController.execute(payoutInputData);
        }
    }

    public void presentFailView(String message) {
        System.out.print(message);
    }
}
