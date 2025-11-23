package use_case.playerStand;

import entity.BlackjackDealer;
import entity.BlackjackGame;
import entity.BlackjackPlayer;
import entity.Card;
import entity.Hand;

public class PlayerStandInteractor implements PlayerStandInputBoundary {

    private final PlayerStandUserDataAccessInterface deck;
    private final PlayerStandOutputBoundary presenter;

    public PlayerStandInteractor(PlayerStandUserDataAccessInterface deck, PlayerStandOutputBoundary presenter) {
        this.deck = deck;
        this.presenter = presenter;
    }

    @Override
    public void execute(PlayerStandInputData inputData) {
        BlackjackGame game = inputData.getBlackjackGame();
        BlackjackDealer dealer = game.getDealer();

        // get current player hand
        Hand playerHand = getCurrentHand(inputData);

        // transition to dealer's turn
        game.toDealerTurn();

        // show dealer's hidden card
        dealer.setHideFirstCard(false);

        // dealer must hit until they reach 17 or more (or bust)
        Hand dealerHand = dealer.getHand();
        if (dealerHand == null) {
            throw new IllegalStateException("Dealer hand has not been initialized");
        }
        
        while (dealerHand.getHandTotalNumber() < 17 && !dealerHand.isBust()) {
            Card newCard = deck.drawCard();
            dealerHand.addCard(newCard);
        }

        // determine winner based on blackjack rules
        determineWinner(game, playerHand, dealerHand);

        // create output data
        PlayerStandOutputData outputData = new PlayerStandOutputData(game, dealerHand, playerHand);
        presenter.present(outputData);
    }

    private Hand getCurrentHand(PlayerStandInputData inputData) {
        BlackjackPlayer player = inputData.getBlackjackPlayer();
        if (player.getHands().isEmpty()) {
            throw new IllegalStateException("Player has no hands");
        }
        
        if (inputData.isInSplittedHand()) {
            if (player.getHands().size() < 2) {
                throw new IllegalStateException("Player does not have a split hand");
            }
            return player.getHands().get(1);
        } else {
            return player.getHands().get(0);
        }
    }

    private void determineWinner(BlackjackGame game, Hand playerHand, Hand dealerHand) {
        int playerTotal = playerHand.getHandTotalNumber();
        int dealerTotal = dealerHand.getHandTotalNumber();

        // if dealer busts, player wins
        if (dealerHand.isBust()) {
            game.playerWin();
        }
        // if player busts (shouldn't happen if they stood, but check anyway), player loses
        else if (playerHand.isBust()) {
            game.playerLose();
        }
        // if player's hand is higher than dealer's, player wins
        else if (playerTotal > dealerTotal) {
            game.playerWin();
        }
        // if dealer's hand is higher than player's, player loses
        else if (dealerTotal > playerTotal) {
            game.playerLose();
        }
        // if they're equal, it's a push (tie)
        else {
            game.push();
        }
    }
}
