package interface_adapter.playerDoubleDown;

import entity.BlackjackGame;
import entity.BlackjackPlayer;
import use_case.playerDoubleDown.PlayerDoubleDownInputBoundary;
import use_case.playerDoubleDown.PlayerDoubleDownInputData;

/**
 * Controller for the Player Double Down use case.
 * Handles user input and delegates to the interactor.
 */
public class PlayerDoubleDownController {
    private final PlayerDoubleDownInputBoundary interactor;

    public PlayerDoubleDownController(PlayerDoubleDownInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes a double down action for the player.
     * @param game the blackjack game
     * @param player the blackjack player
     * @param isInSplittedHand whether the player is playing a split hand
     */
    public void doubleDown(BlackjackGame game, BlackjackPlayer player, boolean isInSplittedHand) {
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, isInSplittedHand);
        interactor.execute(inputData);
    }
}

