package use_case.payout;

import entity.Accounts;
import entity.BlackjackGame;
import entity.Hand;

/**
 * Interactor for handling blackjack payouts and losses.
 */
public class PayoutInteractor {
    private final PayoutUserDataAccessInterface userDataAccessObject;
    private final PayoutOutputBoundary presenter;

    public PayoutInteractor(PayoutUserDataAccessInterface userDataAccessObject,
                           PayoutOutputBoundary presenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.presenter = presenter;
    }

    public void execute(PayoutInputData inputData) {
        BlackjackGame game = inputData.getGame();
        String username = userDataAccessObject.getCurrentUsername();
        Accounts account = userDataAccessObject.get(username);
        
        if (account == null) {
            return;
        }

        int betAmount = (int) game.getBetAmount();
        String result = game.getResult();
        int payoutAmount = 0;
        int currentBalance = account.getBalance();

        // check if player has blackjack (21 with exactly 2 cards)
        boolean playerHasBlackjack = isBlackjack(game);
        boolean dealerHasBlackjack = isDealerBlackjack(game);

        int newBalance;
        if (result.equals("PlayerWin")) {
            if (playerHasBlackjack && !dealerHasBlackjack) {
                // blackjack pays 3:2 (bet was already deducted, so add back bet + 1.5x bet)
                payoutAmount = (int) (betAmount * 1.5);
                newBalance = currentBalance + betAmount + payoutAmount;
            } else {
                // regular win pays 1:1 (bet was already deducted, so add back bet + bet)
                payoutAmount = betAmount;
                newBalance = currentBalance + betAmount + payoutAmount;
            }
        } else if (result.equals("PlayerLose")) {
            // loss: bet was already deducted when placed, so no change
            payoutAmount = -betAmount;
            newBalance = currentBalance;
        } else if (result.equals("Push")) {
            // push: return bet (bet was deducted, so add it back)
            payoutAmount = 0;
            newBalance = currentBalance + betAmount;
        } else {
            // game still in progress or unknown result
            payoutAmount = 0;
            newBalance = currentBalance;
        }

        // update account balance
        account.setBalance(newBalance);
        userDataAccessObject.save(account);

        PayoutOutputData outputData = new PayoutOutputData(newBalance, payoutAmount, result);
        presenter.prepareSuccessView(outputData);
    }

    // check if player has blackjack (21 with exactly 2 cards)
    private boolean isBlackjack(BlackjackGame game) {
        Hand playerHand = game.getPlayer().getHands().get(0);
        return playerHand.getHandTotalNumber() == 21 && playerHand.getCards().size() == 2;
    }

    // check if dealer has blackjack (21 with exactly 2 cards)
    private boolean isDealerBlackjack(BlackjackGame game) {
        Hand dealerHand = game.getDealer().getHand();
        return dealerHand.getHandTotalNumber() == 21 && dealerHand.getCards().size() == 2;
    }
}

