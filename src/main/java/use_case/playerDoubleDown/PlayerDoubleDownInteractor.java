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

        // get the current hand (first hand or split hand)
        Hand currentHand = getCurrentHand(player, isInSplittedHand);

        // validate double down conditions
        String validationError = validateDoubleDown(game, currentHand, isInSplittedHand);
        if (validationError != null) {
            presenter.presentFailView(validationError);
            return;
        }

        // get current bet amount
        int currentBet = (int) game.getBetAmount();
        int additionalBet = currentBet; // Double down means adding the same amount again

        // check if player has sufficient balance
        String username = userDataAccessObject.getCurrentUsername();
        Accounts account = userDataAccessObject.get(username);
        if (account == null) {
            presenter.presentFailView("Account not found.");
            return;
        }

        int currentBalance = account.getBalance();
        if (currentBalance < additionalBet) {
            presenter.presentFailView("Insufficient funds to double down. Need $" + additionalBet + " but only have $" + currentBalance + ".");
            return;
        }

        // Deduct the additional bet from balance
        account.subtractFunds(additionalBet);
        userDataAccessObject.save(account);

        // Double the bet amount in the game
        int newBetAmount = currentBet * 2;
        game.setBetAmount(newBetAmount);

        // Deal exactly one card to the current hand
        Card newCard;
        try {
            newCard = userDataAccessObject.drawCard();
        } catch (Exception e) {
            // If card draw fails, refund the bet and report error
            account.addFunds(additionalBet);
            userDataAccessObject.save(account);
            game.setBetAmount(currentBet);
            presenter.presentFailView("Problem drawing card for double down.");
            return;
        }
        currentHand.addCard(newCard);

        // Check if the hand is bust
        boolean isBust = currentHand.isBust();

        // Double down rules:
        // 1. After double down, player receives exactly one card
        // 2. Player's turn automatically ends (no more hits allowed)
        // 3. If player busts, they lose immediately
        // 4. If player doesn't bust, proceed to dealer's turn (via stand)
        // 5. Payout uses the doubled bet amount
        
        // If bust and game is not split, or if split hand busts, set game result to player loses
        if (isBust) {
            if (!game.isSplitted() || isInSplittedHand) {
                game.playerLose();
            }
        }
        // Note: If not bust, the presenter will trigger stand to proceed to dealer's turn
        // The payout will be calculated using the doubled bet amount when the game ends

        PlayerDoubleDownOutputData outputData = new PlayerDoubleDownOutputData(
                game,
                currentHand,
                isInSplittedHand,
                isBust,
                newBetAmount,
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
}

