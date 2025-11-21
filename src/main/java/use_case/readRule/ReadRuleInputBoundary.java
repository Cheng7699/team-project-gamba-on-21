package use_case.readRule;

import entity.BlackjackGame;

/**
 * Input boundary for action to read rules
 */
public interface ReadRuleInputBoundary {

    /**
     * Execute read rule action
     * @param readRuleInputData the read rule input data
     */
    void execute(ReadRuleInputData readRuleInputData);

}
