package interface_adapter.playerHit;

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
        boolean isBust = outputData.isBust();

        if (outputData.isSplitHand()) {
            view.setHands(view.getPlayerHand(), playerHand, dealerHand, isHideFirstCard);
        }
        else {
            view.setHands(playerHand, view.getSplitHand(), dealerHand, isHideFirstCard);
        }

        if (isBust) {
            // player busts: game over, player loses
            if (view.getGame().isSplitted() && !outputData.isSplitHand()) {
                view.advanceToSplitHand();
                return;
            }

            view.showRoundResult("You Busted!");
            // process payout for loss
            if (payoutController != null) {
                PayoutInputData payoutInputData = new PayoutInputData(view.getGame());
                payoutController.execute(payoutInputData);
                }
            }
    }
}