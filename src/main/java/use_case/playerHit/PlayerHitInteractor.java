package use_case.playerHit;

import entity.BlackjackGame;
import entity.BlackjackPlayer;
import entity.Card;
import entity.Hand;

public class PlayerHitInteractor implements PlayerHitInputBoundary{

    private final PlayerHitUserDataAccessInterface deck;
    private final PlayerHitOutputBoundary presenter;
    private final BlackjackGame game;

    public PlayerHitInteractor(PlayerHitUserDataAccessInterface deck,
                               PlayerHitOutputBoundary presenter, BlackjackGame game) {
        this.deck = deck;
        this.presenter = presenter;
        this.game = game;
    }

    @Override
    public void execute(PlayerHitInputData inputData) {

        // Find out the first hand or the hand after split to draw a card.
        Hand currentHand = getCurrentHand(inputData);

        // Draw a card from the deck.
        Card newCard = deck.drawCard();

        // Add the drawn card to the hand.
        currentHand.addCard(newCard);

        // Check if the hand is bust or not.
        boolean isBust = currentHand.isBust();

        // If the hand is bust, we set the game result to player loses.
        if (isBust) {
            game.playerLose();
        }

        // Checkout if the player is in split hand or not.
        boolean isSplit = inputData.isInSplittedHand();

        PlayerHitOutputData outputData = new PlayerHitOutputData(currentHand, isSplit, isBust);
        presenter.present(outputData);
    }

    private Hand getCurrentHand(PlayerHitInputData inputData) {
        BlackjackPlayer player = inputData.getBlackjackPlayer();
        if (inputData.isInSplittedHand()) {
            return player.getHands().get(1);
        }
        else {
            return player.getHands().get(0); }
    }
}
