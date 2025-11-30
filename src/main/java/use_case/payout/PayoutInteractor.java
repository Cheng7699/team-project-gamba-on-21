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

        // Get the bet amount from game (this will be doubled if player doubled down)
        int betAmount = (int) game.getBetAmount();
        String result = game.getResult();
        int payoutAmount = 0;
        int currentBalance = account.getBalance();

        // check if player has blackjack (21 with exactly 2 cards)
        // Note: After double down, player has 3 cards, so even if total is 21, it's NOT blackjack
        boolean playerHasBlackjack = isBlackjack(game);
        boolean dealerHasBlackjack = isDealerBlackjack(game);

        int newBalance;
        if (result.equals("PlayerWin")) {
            if (playerHasBlackjack && !dealerHasBlackjack) {
                // blackjack pays 3:2 (only possible with initial 2 cards, not after double down)
                // for every $2 bet, you win $3
                // example: bet $100 -> win $150, total return $250 (bet $100 + winnings $150)
                // bet was already deducted, so add back bet (stake) + 1.5x bet winnings
                // Using integer division to ensure correct 3:2 payout
                // For $100 bet: $100 * 3 / 2 = $150 winnings
                payoutAmount = (betAmount * 3) / 2;
                // Return stake + winnings: currentBalance + betAmount (stake) + payoutAmount (winnings)
                newBalance = currentBalance + betAmount + payoutAmount;
            } else {
                // regular win pays 1:1 (includes wins after double down)
                // bet was already deducted (original bet + additional bet for double down = final bet)
                // For 1:1 payout: return stake + winnings equal to stake
                // Total to add: 2 * betAmount (stake return + winnings)
                // Net profit: betAmount
                payoutAmount = betAmount; // net profit for 1:1 win
                // Return stake + winnings: currentBalance + betAmount (stake) + betAmount (winnings)
                newBalance = currentBalance + betAmount + betAmount;
            }
        } else if (result.equals("PlayerLose")) {
            // loss: bet was already deducted (original + additional for double down = final bet)
            // no change to balance (bet stays deducted)
            payoutAmount = -betAmount; // negative indicates loss
            newBalance = currentBalance;
        } else if (result.equals("Push")) {
            // push: return bet (bet was deducted, so add it back)
            // for double down, this returns the final bet amount
            payoutAmount = 0; // no profit/loss on push
            newBalance = currentBalance + betAmount; // return stake only
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

    /**
     * Checks if player has blackjack (21 with exactly 2 cards).
     * Note: After double down, player has 3 cards, so even if total is 21, it's not blackjack.
     * @param game the blackjack game
     * @return true if player has blackjack (21 with exactly 2 cards)
     */
    private boolean isBlackjack(BlackjackGame game) {
        Hand playerHand = game.getPlayer().getHands().get(0);
        // Blackjack is only 21 with exactly 2 cards (not after double down or hit)
        return playerHand.getHandTotalNumber() == 21 && playerHand.getCards().size() == 2;
    }

    // check if dealer has blackjack (21 with exactly 2 cards)
    private boolean isDealerBlackjack(BlackjackGame game) {
        Hand dealerHand = game.getDealer().getHand();
        return dealerHand.getHandTotalNumber() == 21 && dealerHand.getCards().size() == 2;
    }
}

