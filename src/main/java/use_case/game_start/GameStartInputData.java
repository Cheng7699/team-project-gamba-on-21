package use_case.game_start;


import entity.BlackjackGame;

public class GameStartInputData {
    private final BlackjackGame game;
    private final Integer betAmount;

    public GameStartInputData(BlackjackGame game, Integer betAmount) {
        this.game = game;
        this.betAmount = betAmount;
    }

    public BlackjackGame getGame() { return game; }
    public Integer getBetAmount() { return betAmount; }
}
