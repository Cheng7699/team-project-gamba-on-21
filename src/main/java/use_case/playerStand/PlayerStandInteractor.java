package use_case.playerStand;

import entity.BlackjackDealer;
import entity.BlackjackGame;
import entity.BlackjackPlayer;
import entity.Card;
import entity.Hand;

import java.io.IOException;

public class PlayerStandInteractor implements PlayerStandInputBoundary {

    private final BlackjackGame game;
    private final PlayerStandUserDataAccessInterface apiClient;
    private final PlayerStandOutputBoundary presenter;

    public PlayerStandInteractor(BlackjackGame game, PlayerStandUserDataAccessInterface apiClient, PlayerStandOutputBoundary presenter) {
        this.game = game;
        this.apiClient = apiClient;
        this.presenter = presenter;
    }

    @Override
    public void execute(PlayerStandInputData inputData) {
        BlackjackGame game = inputData.getGame();
        BlackjackDealer dealer = game.getDealer();

        // get current player hand
        Hand playerHand = getCurrentHand();

        // transition to dealer's turn
        game.toDealerTurn();

        // show dealer's hidden card
        dealer.setHideFirstCard(false);

        /** Tried to make it show the cards one by one
        presenter.present(new PlayerStandOutputData(game));
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            presenter.presentFailView("process interrupted");
        }
         */

        // dealer must hit until they reach 17 or more (or bust)
        Hand dealerHand = dealer.getHand();
        if (dealerHand == null) {
            throw new IllegalStateException("Dealer hand has not been initialized");
        }
        
        while (dealerHand.getHandTotalNumber() < 17 && !dealerHand.isBust()) {
            try{
                Card newCard = apiClient.drawCard(game.getDeckID());
                dealerHand.addCard(newCard);

                /** Tried to make it show the cards one by one
                presenter.present(new PlayerStandOutputData(game));
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    presenter.presentFailView("process interrupted");
                }
                 */
            }
            catch (IOException e) {
                presenter.presentFailView("Problem drawing cards");
            }

        }

        // determine winner based on blackjack rules
        determineWinner(game, playerHand, dealerHand);

        // create output data
        PlayerStandOutputData outputData = new PlayerStandOutputData(game);
        presenter.present(outputData);
        
        // note: payout is handled by the presenter after game ends
    }

    private Hand getCurrentHand() {
        BlackjackPlayer player = game.getPlayer();
        if (player.getHands().isEmpty()) {
            throw new IllegalStateException("Player has no hands");
        }
        
        if (game.isSplitted()) {
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
        boolean playerHasBlackjack = playerTotal == 21 && playerHand.getCards().size() == 2;
        boolean dealerHasBlackjack = dealerTotal == 21 && dealerHand.getCards().size() == 2;

        // check for blackjack first (21 with exactly 2 cards)
        // both have blackjack: push
        if (playerHasBlackjack && dealerHasBlackjack) {

            if (!game.isSplitted() || !game.getState().equals("InGame")) {
                game.push();
            }
            else {
                game.splitPlayerPush();
            }

        // player has blackjack, dealer doesn't: player wins
        } else if (playerHasBlackjack) {
            if (!game.isSplitted() || !game.getState().equals("InGame")) {
                game.playerWin();
            }
            else {
                game.splitPlayerWin();
            }

        // dealer has blackjack, player doesn't: player loses
        } else if (dealerHasBlackjack) {
            if (!game.isSplitted() || !game.getState().equals("InGame")) {
                game.playerLose();
            }
            else {
                game.splitPlayerLose();
            }
        }
        // if dealer busts, player wins
        else if (dealerHand.isBust()) {
            game.playerWin();
            game.splitPlayerWin();
        }

        // if player busts (shouldn't happen if they stood, but check anyway), player loses
        else if (playerHand.isBust()) {
            if (!game.isSplitted() || !game.getState().equals("InGame")) {
                game.playerLose();
            }
            else {
                game.splitPlayerLose();
            }
        }

        // if player's hand is higher than dealer's, player wins
        else if (playerTotal > dealerTotal) {
            if (!game.isSplitted() || !game.getState().equals("InGame")) {
                game.playerWin();
            }
            else {
                game.splitPlayerWin();
            }
        }


        // if dealer's hand is higher than player's, player loses
        else if (dealerTotal > playerTotal) {
            if (!game.isSplitted() || !game.getState().equals("InGame")) {
                game.playerLose();
            }
            else {
                game.splitPlayerLose();
            }
        }


        // if they're equal, it's a push (tie)
        else {
            if (!game.isSplitted() || !game.getState().equals("InGame")) {
                game.push();
            }
            else {
                game.splitPlayerPush();
            }
        }
    }
}
