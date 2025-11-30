package use_case.playerSplit;

import entity.BlackjackGame;

public class PlayerSplitOutputData {
    private final BlackjackGame game;

    public PlayerSplitOutputData(BlackjackGame game) {
        this.game = game;
    }

    public BlackjackGame getGame() { return game; }
}
