package use_case.playerStand;

import entity.BlackjackGame;
import entity.Hand;

public class PlayerStandOutputData {
    private final BlackjackGame blackjackGame;

    public PlayerStandOutputData(BlackjackGame blackjackGame) {
        this.blackjackGame = blackjackGame;
    }

    public BlackjackGame getGame() { return blackjackGame; }
}
