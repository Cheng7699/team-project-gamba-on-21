package use_case.playerDoubleDown;

import entity.Accounts;
import entity.BlackjackGame;
import entity.BlackjackPlayer;
import entity.Card;
import entity.Hand;

/**
 * Implements logic for doubling down in blackjack:
 * - Validates that double down is allowed (exactly 2 cards, hasn't hit, sufficient balance)
 * - Doubles the bet and deducts the additional amount from balance
 * - Deals exactly one card to the player's hand
 * - Automatically ends the player's turn (no more actions allowed)
 */
public class PlayerDoubleDownInteractor implements PlayerDoubleDownInputBoundary {
    private final PlayerDoubleDownUserDataAccessInterface userDataAccessObject;
    private final PlayerDoubleDownOutputBoundary presenter;

    public PlayerDoubleDownInteractor(PlayerDoubleDownUserDataAccessInterface userDataAccessObject,
                                     PlayerDoubleDownOutputBoundary presenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(PlayerDoubleDownInputData inputData) {
        BlackjackGame game = inputData.getGame();
        BlackjackPlayer player = inputData.getPlayer();
        boolean isInSplittedHand = inputData.isInSplittedHand();

        Hand currentHand = getCurrentHand(player, isInSplittedHand);

        // validate double down conditions
        String validationError = validateDoubleDown(game, currentHand, isInSplittedHand);
        if (validationError != null) {
            presenter.presentFailView(validationError);
            return;
        }

        // validate and retrieve account
        Accounts account = getAccount();
        if (account == null) {
            return; // Error already presented
        }

        // validate sufficient balance
        int currentBet = (int) game.getBetAmount();
        int additionalBet = calculateAdditionalBet(currentBet);
        if (!hasSufficientBalance(account, additionalBet)) {
            return; // Error already presented
        }

        // process double down: deduct bet, double bet amount, deal card
        DoubleDownResult result = processDoubleDown(game, account, currentBet, additionalBet, currentHand);
        if (!result.isSuccess()) {
            return; // Error already presented
        }

        // determine game outcome and create output
        boolean isBust = result.getHand().isBust();
        updateGameResultIfBust(game, isBust, isInSplittedHand);

        PlayerDoubleDownOutputData outputData = new PlayerDoubleDownOutputData(
                game,
                result.getHand(),
                isInSplittedHand,
                isBust,
                result.getNewBetAmount(),
                account.getBalance()
        );
        presenter.present(outputData);
    }

    /**
     * Validates that double down is allowed for the current hand.
     * @param game the blackjack game
     * @param currentHand the current hand to validate
     * @param isInSplittedHand whether this is a split hand
     * @return null if valid, error message if invalid
     */
    private String validateDoubleDown(BlackjackGame game, Hand currentHand, boolean isInSplittedHand) {
        // Check if hand exists
        if (currentHand == null || currentHand.getCards().isEmpty()) {
            return "No hand available to double down.";
        }

        // Double down only allowed with exactly 2 cards
        if (currentHand.getCards().size() != 2) {
            return "Double down is only allowed with exactly 2 cards. You have " + currentHand.getCards().size() + " cards.";
        }

        // Check if hand is already bust (shouldn't happen with 2 cards, but check anyway)
        if (currentHand.isBust()) {
            return "Cannot double down on a bust hand.";
        }

        // Check if game result indicates game is over
        // We check result instead of state because state might not be set correctly
        String gameResult = game.getResult();
        if (gameResult != null && !"InGame".equals(gameResult)) {
            // Game has a result (PlayerWin, PlayerLose, Push) - game is over
            return "Cannot double down after game is over.";
        }

        return null; // Valid
    }

    /**
     * Gets the current hand (first hand or split hand).
     * @param player the blackjack player
     * @param isInSplittedHand whether to get the split hand
     * @return the current hand, or null if no hands exist
     */
    private Hand getCurrentHand(BlackjackPlayer player, boolean isInSplittedHand) {
        if (player == null || player.getHands() == null || player.getHands().isEmpty()) {
            return null;
        }
        
        if (isInSplittedHand && player.getHands().size() > 1) {
            return player.getHands().get(1);
        } else {
            return player.getHands().get(0);
        }
    }

    /**
     * Retrieves the account for the current user.
     * @return the account, or null if not found (error already presented)
     */
    private Accounts getAccount() {
        String username = userDataAccessObject.getCurrentUsername();
        Accounts account = userDataAccessObject.get(username);
        if (account == null) {
            presenter.presentFailView("Account not found.");
        }
        return account;
    }

    /**
     * Calculates the additional bet amount for double down.
     * @param currentBet the current bet amount
     * @return the additional bet (equal to current bet for double down)
     */
    private int calculateAdditionalBet(int currentBet) {
        return currentBet; // Double down means adding the same amount again
    }

    /**
     * Validates that the player has sufficient balance for double down.
     * @param account the player's account
     * @param additionalBet the additional bet amount required
     * @return true if sufficient balance, false otherwise (error already presented)
     */
    private boolean hasSufficientBalance(Accounts account, int additionalBet) {
        int currentBalance = account.getBalance();
        if (currentBalance < additionalBet) {
            presenter.presentFailView("Insufficient funds to double down. Need $" + additionalBet + " but only have $" + currentBalance + ".");
            return false;
        }
        return true;
    }

    /**
     * Result class to encapsulate the outcome of processing double down.
     * Follows Single Responsibility Principle by separating result data.
     */
    private static class DoubleDownResult {
        private final boolean success;
        private final Hand hand;
        private final int newBetAmount;

        private DoubleDownResult(boolean success, Hand hand, int newBetAmount) {
            this.success = success;
            this.hand = hand;
            this.newBetAmount = newBetAmount;
        }

        static DoubleDownResult success(Hand hand, int newBetAmount) {
            return new DoubleDownResult(true, hand, newBetAmount);
        }

        static DoubleDownResult failure() {
            return new DoubleDownResult(false, null, 0);
        }

        boolean isSuccess() { return success; }
        Hand getHand() { return hand; }
        int getNewBetAmount() { return newBetAmount; }
    }

    /**
     * Processes the double down operation: deducts bet, doubles bet amount, and deals card.
     * Follows Single Responsibility Principle by handling all double down processing logic.
     * @param game the blackjack game
     * @param account the player's account
     * @param currentBet the current bet amount
     * @param additionalBet the additional bet to deduct
     * @param currentHand the current hand to add card to
     * @return result containing the updated hand and new bet amount, or failure result
     */
    private DoubleDownResult processDoubleDown(BlackjackGame game, Accounts account, 
                                               int currentBet, int additionalBet, Hand currentHand) {
        // Deduct the additional bet from balance
        account.subtractFunds(additionalBet);
        userDataAccessObject.save(account);

        // Double the bet amount in the game
        int newBetAmount = currentBet * 2;
        game.setBetAmount(newBetAmount);

        // Deal exactly one card to the current hand
        try {
            Card newCard = userDataAccessObject.drawCard();
            currentHand.addCard(newCard);
            return DoubleDownResult.success(currentHand, newBetAmount);
        } catch (Exception e) {
            // If card draw fails, rollback: refund the bet and restore original bet amount
            rollbackDoubleDown(account, additionalBet, game, currentBet);
            presenter.presentFailView("Problem drawing card for double down.");
            return DoubleDownResult.failure();
        }
    }

    /**
     * Rolls back a failed double down operation by refunding the bet and restoring the original bet amount.
     * Follows Single Responsibility Principle by handling rollback logic separately.
     * @param account the player's account
     * @param additionalBet the additional bet to refund
     * @param game the blackjack game
     * @param originalBet the original bet amount to restore
     */
    private void rollbackDoubleDown(Accounts account, int additionalBet, BlackjackGame game, int originalBet) {
        account.addFunds(additionalBet);
        userDataAccessObject.save(account);
        game.setBetAmount(originalBet);
    }

    /**
     * Updates the game result if the hand is bust.
     * Follows Single Responsibility Principle by handling game state updates separately.
     * @param game the blackjack game
     * @param isBust whether the hand is bust
     * @param isInSplittedHand whether this is a split hand
     */
    private void updateGameResultIfBust(BlackjackGame game, boolean isBust, boolean isInSplittedHand) {
        if (isBust) {
            // If bust and game is not split, or if split hand busts, set game result to player loses
            if (!game.isSplitted() || isInSplittedHand) {
                game.playerLose();
            }
        }
    }
}

