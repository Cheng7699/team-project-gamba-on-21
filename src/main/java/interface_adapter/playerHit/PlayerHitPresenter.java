package interface_adapter.playerHit;

import entity.Hand;
import use_case.playerHit.PlayerHitOutputBoundary;
import use_case.playerHit.PlayerHitOutputData;
import view.BlackjackView;

public class PlayerHitPresenter implements PlayerHitOutputBoundary {

    private BlackjackView view;

    public PlayerHitPresenter(BlackjackView view) {
        this.view = view;
    }

    @Override
    public void present(PlayerHitOutputData outputData) {

        Hand playerHand = outputData.getHandAfterHit();
        Hand dealerHand = view.getDealerHand();
        boolean isHideFirstCard = view.isHideDealerHoleCard();

        view.setHands(playerHand, dealerHand, isHideFirstCard);

        if (playerHand.isBust()) { view.showRoundResult("You Busted!"); }
    }
}