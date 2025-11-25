package use_case.game_start;


import entity.BlackjackGame;

public class GameStartInputData {
    private final BlackjackGame game;

    public GameStartInputData(BlackjackGame game) {
        this.game = game;
    }

    public BlackjackGame getGame() { return game; }
}
