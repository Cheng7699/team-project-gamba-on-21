package use_case.playerHit;

import entity.BlackjackPlayer;
import entity.Card;
import entity.Hand;

public class PlayerHitInteractor implements PlayerHitInputBoundary{

    private final PlayerHitUserDataAccessInterface deck;
    private final PlayerHitOutputBoundary presenter;

    public PlayerHitInteractor(PlayerHitUserDataAccessInterface deck, PlayerHitOutputBoundary presenter) {
        this.deck = deck;
        this.presenter = presenter;
    }

    @Override
    public void execute(PlayerHitInputData inputData) {
        BlackjackPlayer player = inputData.getBlackjackPlayer();
        Hand currentHand = getCurrentHand(inputData);

        Card newCard = deck.drawCard();
        currentHand.addCard(newCard);

        PlayerHitOutputData outputData = new PlayerHitOutputData(currentHand);
        presenter.present(outputData);
    }

    private Hand getCurrentHand(PlayerHitInputData inputData) {
        BlackjackPlayer player = inputData.getBlackjackPlayer();
        if (inputData.isInSplittedHand()) {
            return player.getHands().get(1);
        }
        else { return player.getHands().get(0); }
    }
}
