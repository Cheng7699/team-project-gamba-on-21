package use_case.playerStand;

import entity.BlackjackGame;
import entity.BlackjackPlayer;

public class PlayerStandInputData {
    private final BlackjackGame blackjackGame;

    public PlayerStandInputData(BlackjackGame blackjackGame) {
        this.blackjackGame = blackjackGame;
    }

    public BlackjackGame getGame() { return blackjackGame; }
}

