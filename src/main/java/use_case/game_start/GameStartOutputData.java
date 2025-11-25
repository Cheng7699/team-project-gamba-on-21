package use_case.game_start;

import entity.BlackjackGame;

public class GameStartOutputData {
    private final BlackjackGame game;
    public GameStartOutputData(BlackjackGame game) {
        this.game = game;
    }
    public BlackjackGame getGame() { return game; }
}
