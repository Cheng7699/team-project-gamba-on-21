package interface_adapter.playerDoubleDown;

import entity.BlackjackGame;
import entity.Hand;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.payout.PayoutController;
import use_case.playerDoubleDown.PlayerDoubleDownOutputBoundary;
import use_case.playerDoubleDown.PlayerDoubleDownOutputData;
import use_case.payout.PayoutInputData;
import view.BlackjackView;

/**
 * Presenter for the Player Double Down use case.
 * Updates the view and view model based on the double down result.
 */
public class PlayerDoubleDownPresenter implements PlayerDoubleDownOutputBoundary {
    private final BlackjackView view;
    private final LoggedInViewModel loggedInViewModel;
    private PayoutController payoutController;

    public PlayerDoubleDownPresenter(BlackjackView view, LoggedInViewModel loggedInViewModel) {
        this.view = view;
        this.loggedInViewModel = loggedInViewModel;
    }

    public void setPayoutController(PayoutController payoutController) {
        this.payoutController = payoutController;
    }

    @Override
    public void present(PlayerDoubleDownOutputData outputData) {
        BlackjackGame game = outputData.getGame();
        Hand handAfterDoubleDown = outputData.getHandAfterDoubleDown();
        boolean isSplitHand = outputData.isSplitHand();
        boolean isBust = outputData.isBust();

        Hand dealerHand = view.getDealerHand();
        boolean isHideFirstCard = view.isHideDealerHoleCard();

        // Update the view with the new hand
        if (isSplitHand) {
            view.setHands(view.getPlayerHand(), handAfterDoubleDown, dealerHand, isHideFirstCard);
        } else {
            view.setHands(handAfterDoubleDown, view.getSplitHand(), dealerHand, isHideFirstCard);
        }

        // Update bet display
        view.updateBetDisplay(outputData.getNewBetAmount());

        // Update balance in view model
        interface_adapter.logged_in.LoggedInState state = loggedInViewModel.getState();
        state.setBalance(outputData.getNewBalance());
        loggedInViewModel.setState(state);
        loggedInViewModel.firePropertyChange();
        loggedInViewModel.firePropertyChange("balance");

        // Disable double down, hit, and split buttons (double down ends player's turn)
        view.disablePlayerActions();

        if (isBust) {
            // Player busts: game over, player loses
            if (game.isSplitted() && !isSplitHand) {
                // If playing split and first hand busts, advance to split hand
                view.advanceToSplitHand();
                return;
            }

            view.showRoundResult("You Busted!");
            // Process payout for loss
            if (payoutController != null) {
                PayoutInputData payoutInputData = new PayoutInputData(game);
                payoutController.execute(payoutInputData);
            }
        } else {
            // Double down automatically ends player's turn - proceed to dealer or next split hand
            // After double down (if not bust), player must stand and dealer plays
            // Payout will be calculated using the doubled bet amount when game ends
            if (game.isSplitted() && !isSplitHand) {
                // If playing split and this is the first hand, advance to split hand
                view.advanceToSplitHand();
                view.showStatusMessage("Double down complete. Playing split hand now.");
            } else {
                // Otherwise, automatically stand (proceed to dealer's turn)
                // The stand action will trigger dealer play, determine winner, and call payout
                view.showStatusMessage("Double down complete. Proceeding to dealer's turn...");
                // Trigger stand action to proceed to dealer
                // Payout will be called from PlayerStandPresenter after dealer plays
                if (view.getStandActionListener() != null) {
                    view.getStandActionListener().actionPerformed(
                            new java.awt.event.ActionEvent(view, java.awt.event.ActionEvent.ACTION_PERFORMED, "stand"));
                }
            }
        }
    }

    @Override
    public void presentFailView(String errorMessage) {
        view.showStatusMessage(errorMessage);
    }
}

