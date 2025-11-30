package use_case.playerSplit;

import entity.BlackjackGame;

public class PlayerSplitInputData {
    private final BlackjackGame game;

    public PlayerSplitInputData(BlackjackGame game) {
        this.game = game;
    }

    public BlackjackGame getGame() { return game; }
}
