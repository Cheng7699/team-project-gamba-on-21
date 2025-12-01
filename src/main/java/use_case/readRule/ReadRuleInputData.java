package use_case.readRule;

import entity.BlackjackGame;

/**
 * The input or initial data needed for the read rule use case
 */
public class ReadRuleInputData {

    private final BlackjackGame blackjackGame;

    public ReadRuleInputData(BlackjackGame blackjackGame) {
        this.blackjackGame = blackjackGame;
    }

    public BlackjackGame getBlackjackGame() {
        return blackjackGame;
    }
}
