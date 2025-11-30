package use_case.payout;

import entity.BlackjackGame;

/**
 * Input data for payout use case.
 */
public class PayoutInputData {
    private final BlackjackGame game;

    public PayoutInputData(BlackjackGame game) {
        this.game = game;
    }

    public BlackjackGame getGame() {
        return game;
    }
}

