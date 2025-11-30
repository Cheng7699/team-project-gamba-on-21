package use_case.playerDoubleDown;

import entity.BlackjackGame;
import entity.BlackjackPlayer;

public class PlayerDoubleDownInputData {
    private final BlackjackGame game;
    private final BlackjackPlayer player;
    private final boolean isInSplittedHand;

    public PlayerDoubleDownInputData(BlackjackGame game, BlackjackPlayer player, boolean isInSplittedHand) {
        this.game = game;
        this.player = player;
        this.isInSplittedHand = isInSplittedHand;
    }

    public BlackjackGame getGame() {
        return game;
    }

    public BlackjackPlayer getPlayer() {
        return player;
    }

    public boolean isInSplittedHand() {
        return isInSplittedHand;
    }
}

