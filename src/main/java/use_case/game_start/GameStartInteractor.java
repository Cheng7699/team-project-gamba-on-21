package use_case.game_start;

import entity.BlackjackDealer;
import entity.BlackjackGame;
import entity.Card;
import entity.Hand;
import use_case.launch.LaunchInteractor;

import java.io.IOException;

public class GameStartInteractor implements GameStartInputBoundary {

    private final GameStartOutputBoundary presenter;
    private final GameStartDataAccessInterface apiClient;

    public GameStartInteractor(GameStartDataAccessInterface apiClient, GameStartOutputBoundary presenter) {
        this.presenter = presenter;
        this.apiClient = apiClient;
    }

    @Override
    public void execute(GameStartInputData inputData) {
        BlackjackGame game = inputData.getGame();

        // Reset game result and state at the start of every new round so that
        // use cases like Double Down see the game as \"in progress\" again.
        // Without this, result may still be PlayerWin/PlayerLose/Push from the
        // previous round, causing validations to think the game is over.
        game.setResult("InGame");
        game.toPlayerTurn();

        // If the bet is 0, do nothing
        if (inputData.getBetAmount() == 0) {
            presenter.present(new GameStartOutputData(game, inputData.getBetAmount()));
        }

        // If a deck already exists, just shuffle it.
        if (!game.getDeckID().isEmpty()) {
            try {
                apiClient.shuffle(game.getDeckID());
            }
            catch (IOException e) {
                presenter.presentFailView("Error while shuffling deck");
            }
        }

        // Otherwise, create a new deck
        else{
            try {
                String gameDeck = apiClient.createDeck(true, false);
                game.setDeckID(gameDeck);
            }
            catch (IOException e) {
                game.setDeckID("error in deck creation");
            }
        }

        // Clear any previous hands (including stale split hands) before starting a new round
        game.getPlayer().getHands().clear();
        game.resetSplit();


        // Create the Arrays of cards for the dealer and the player
        Card[] dealerCards = null;
        Card[] playerCards;

        //Draw 2 cards for each (the player and the dealer)
        try {
            dealerCards = apiClient.drawCards(game.getDeckID(), 2);
            playerCards = apiClient.drawCards(game.getDeckID(), 2);
        }
        catch (IOException e) {
            presenter.presentFailView("error in drawing cards");
            return;
        }

        // Store the drawn cards to piles in the API
        try{
            apiClient.addCards(game.getDeckID(), "dealerHand", dealerCards);
            apiClient.addCards(game.getDeckID(), "playerHand1", playerCards);
        }
        catch (IOException e) {
            presenter.presentFailView("error in adding cards to hands");
        }

        // Create new hand objects in the BlackJackGame and give their respective cards.
        game.getDealer().setHand(new Hand("dealerHand"));
        game.getDealer().getHand().addCards(dealerCards);
        game.getPlayer().addHand(new Hand("playerHand1"));
        game.getPlayer().getHands().get(0).addCards(playerCards);

        // Pass the output data (the BlackJackGame and the bet amount) to the presenter
        presenter.present(new GameStartOutputData(game, inputData.getBetAmount()));


    }
}
