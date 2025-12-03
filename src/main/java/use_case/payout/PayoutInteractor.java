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

        // check if dealer has blackjack (21 with exactly 2 cards)
        boolean dealerHasBlackjack = isDealerBlackjack(game);

        int newBalance;
        if (result.equals("PlayerWin")) {
            boolean firstHandHasBlackjack = isBlackjack(game, 0);
            if (firstHandHasBlackjack && !dealerHasBlackjack) {
                payoutAmount = (betAmount * 3) / 2;
                newBalance = currentBalance + betAmount + payoutAmount;
            } else {

                payoutAmount = betAmount;
                newBalance = currentBalance + betAmount + betAmount;
            }
        } else if (result.equals("PlayerLose")) {
            payoutAmount = -betAmount;
            newBalance = currentBalance;
        } else if (result.equals("Push")) {

            payoutAmount = 0;
            newBalance = currentBalance + betAmount;
        } else {
            // game still in progress or unknown result
            payoutAmount = 0;
            newBalance = currentBalance;
        }

        if (game.isSplitted()) {
            String secondResult = game.getSecondResult();
            if (secondResult.equals("PlayerWin")) {
                boolean secondHandHasBlackjack = isBlackjack(game, 1);
                if (secondHandHasBlackjack && !dealerHasBlackjack) {
                    payoutAmount += (betAmount * 3) / 2;
                    newBalance += betAmount + (betAmount * 3) / 2;
                } else {
                    payoutAmount += betAmount;
                    newBalance += betAmount + betAmount;
                }
            } else if (secondResult.equals("PlayerLose")) {
                payoutAmount -= betAmount;
                newBalance -= betAmount;
            } else if (secondResult.equals("Push")) {
                payoutAmount += 0;
                newBalance += betAmount;
            } else {
                payoutAmount += 0;
            }
        }
        account.setBalance(newBalance);
        userDataAccessObject.save(account);

        PayoutOutputData outputData = new PayoutOutputData(newBalance, payoutAmount, result);
        presenter.prepareSuccessView(outputData);
    }

    /**
     * Checks if a specific player hand has blackjack (21 with exactly 2 cards).
     * Note: After double down, player has 3 cards, so even if total is 21, it's not blackjack.
     * @param game the blackjack game
     * @param handIndex the index of the hand to check (0 for first hand, 1 for split hand)
     * @return true if the specified hand has blackjack (21 with exactly 2 cards)
     */
    private boolean isBlackjack(BlackjackGame game, int handIndex) {
        if (game.getPlayer().getHands().size() <= handIndex) {
            return false;
        }
        Hand playerHand = game.getPlayer().getHands().get(handIndex);
        return playerHand.getHandTotalNumber() == 21 && playerHand.getCards().size() == 2;
    }

    /**
     * Checks if dealer has blackjack (21 with exactly 2 cards).
     * @param game the blackjack game
     * @return true if dealer has blackjack (21 with exactly 2 cards)
     */
    private boolean isDealerBlackjack(BlackjackGame game) {
        Hand dealerHand = game.getDealer().getHand();
        if (dealerHand == null) {
            return false;
        }
        return dealerHand.getHandTotalNumber() == 21 && dealerHand.getCards().size() == 2;
    }
}

