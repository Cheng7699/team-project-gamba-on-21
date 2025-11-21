package use_case.readRule;

import entity.BlackjackGame;

/**
 * The input or initial data needed for the read rule use case
 */
public class ReadRuleInputData {

    private float odds;
    private BlackjackGame blackjackGame;

    public ReadRuleInputData(BlackjackGame blackjackGame) {
        this.odds = 0.42f;
        this.blackjackGame = blackjackGame;
    }
}
