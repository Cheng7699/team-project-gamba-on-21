package use_case.game_start;

import entity.BlackjackDealer;
import entity.BlackjackGame;
import entity.Card;
import entity.Hand;
import use_case.launch.LaunchInteractor;

import java.io.IOException;

public class GameStartInteractor implements GameStartInputBoundary {

    private final BlackjackGame game;
    private final GameStartOutputBoundary presenter;
    private final GameStartDataAccessInterface apiClient;

    public GameStartInteractor(BlackjackGame game, GameStartDataAccessInterface apiClient, GameStartOutputBoundary presenter) {
        this.game = game;
        this.presenter = presenter;
        this.apiClient = apiClient;
    }

    @Override
    public void execute(GameStartInputData inputData) {
        BlackjackGame game = inputData.getGame();
        // If a deck already exists, just shuffle it.
        if (!game.getDeckID().isEmpty()) {
            try {
                apiClient.shuffle(game.getDeckID());
            }
            catch (IOException e) {
                presenter.presentFailView("Error while shuffling deck");
            }
        }

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


        Card[] dealerCards = null;
        Card[] playerCards;
        try {
            dealerCards = apiClient.drawCards(game.getDeckID(), 2);
            playerCards = apiClient.drawCards(game.getDeckID(), 2);
        }
        catch (IOException e) {
            presenter.presentFailView("error in drawing cards");
            return;
        }

        try{
            apiClient.addCards(game.getDeckID(), "dealerHand", dealerCards);
            apiClient.addCards(game.getDeckID(), "playerHand1", playerCards);

        }
        catch (IOException e) {
            presenter.presentFailView("error in adding cards to hands");
        }

        game.getDealer().setHand(new Hand("dealerHand"));
        game.getDealer().getHand().addCards(dealerCards);
        game.getPlayer().addHand(new Hand("playerHand1"));
        game.getPlayer().getHands().get(0).addCards(playerCards);

        presenter.present(new GameStartOutputData(game));


    }
}
