package use_case.playerSplit;

import entity.BlackjackGame;
import entity.BlackjackPlayer;
import entity.Card;
import entity.Hand;

import java.io.IOException;

public class PlayerSplitInteractor implements PlayerSplitInputBoundary {

    private final BlackjackGame game;
    private final PlayerSplitDataAccessInterface deckAccess;
    private final PlayerSplitOutputBoundary presenter;

    public PlayerSplitInteractor(BlackjackGame game, PlayerSplitDataAccessInterface deckAccess,
                                 PlayerSplitOutputBoundary presenter) {
        this.game = game;
        this.deckAccess = deckAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(PlayerSplitInputData inputData) {
        BlackjackGame game = inputData.getGame();
        BlackjackPlayer player = game.getPlayer();

        if (game.isSplitted()) {
            presenter.presentFailView("You have already split this hand.");
            return;
        }

        if (player.getHands().isEmpty()) {
            presenter.presentFailView("No hand available to split.");
            return;
        }

        Hand firstHand = player.getHands().get(0);

        if (!firstHand.splittable()) {
            presenter.presentFailView("Current hand cannot be split.");
            return;
        }

        Card cardToMove = firstHand.removeCard(1);
        Hand secondHand = new Hand("playerHand2");
        secondHand.addCard(cardToMove);

        try {
            firstHand.addCard(deckAccess.drawCard(game.getDeckID()));
            secondHand.addCard(deckAccess.drawCard(game.getDeckID()));
        }
        catch (IOException e) {
            presenter.presentFailView("Problem drawing cards for split.");
            return;
        }

        player.addHand(secondHand);
        game.setSplitted(true);

        PlayerSplitOutputData outputData = new PlayerSplitOutputData(game);
        presenter.present(outputData);
    }
}
