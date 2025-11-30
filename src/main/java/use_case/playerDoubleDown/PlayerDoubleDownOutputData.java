package use_case.playerDoubleDown;

import entity.BlackjackGame;
import entity.Hand;

public class PlayerDoubleDownOutputData {
    private final BlackjackGame game;
    private final Hand handAfterDoubleDown;
    private final boolean isSplitHand;
    private final boolean isBust;
    private final int newBetAmount;
    private final int newBalance;

    public PlayerDoubleDownOutputData(BlackjackGame game, Hand handAfterDoubleDown,
                                     boolean isSplitHand, boolean isBust,
                                     int newBetAmount, int newBalance) {
        this.game = game;
        this.handAfterDoubleDown = handAfterDoubleDown;
        this.isSplitHand = isSplitHand;
        this.isBust = isBust;
        this.newBetAmount = newBetAmount;
        this.newBalance = newBalance;
    }

    public BlackjackGame getGame() {
        return game;
    }

    public Hand getHandAfterDoubleDown() {
        return handAfterDoubleDown;
    }

    public boolean isSplitHand() {
        return isSplitHand;
    }

    public boolean isBust() {
        return isBust;
    }

    public int getNewBetAmount() {
        return newBetAmount;
    }

    public int getNewBalance() {
        return newBalance;
    }
}

