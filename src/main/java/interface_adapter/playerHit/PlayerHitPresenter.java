package interface_adapter.playerHit;

import entity.BlackjackGame;
import entity.Hand;
import interface_adapter.payout.PayoutController;
import use_case.playerHit.PlayerHitOutputBoundary;
import use_case.playerHit.PlayerHitOutputData;
import use_case.payout.PayoutInputData;
import view.BlackjackView;

public class PlayerHitPresenter implements PlayerHitOutputBoundary {

    private BlackjackView view;
    private PayoutController payoutController;

    public PlayerHitPresenter(BlackjackView view) {
        this.view = view;
    }

    public void setPayoutController(PayoutController payoutController) {
        this.payoutController = payoutController;
    }

    @Override
    public void present(PlayerHitOutputData outputData) {

        Hand playerHand = outputData.getHandAfterHit();
        Hand dealerHand = view.getDealerHand();
        boolean isHideFirstCard = view.isHideDealerHoleCard();

        view.setHands(playerHand, dealerHand, isHideFirstCard);

        BlackjackGame game = view.getGame();
        
        if (playerHand.isBust()) {
            // player busts: game over, player loses
            if (game != null) {
                game.playerLose();
                view.showRoundResult("You Busted!");
                // process payout for loss
                if (payoutController != null) {
                    PayoutInputData payoutInputData = new PayoutInputData(game);
                    payoutController.execute(payoutInputData);
                }
            }
        } else if (dealerHand.isBust()) { 
            view.showRoundResult("You Won!"); 
        }
    }
}