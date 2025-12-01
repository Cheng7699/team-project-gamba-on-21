package interface_adapter.readrule;

import entity.BlackjackGame;
import use_case.readRule.ReadRuleInputBoundary;
import use_case.readRule.ReadRuleInputData;

public class ReadRuleController {
    private final ReadRuleInputBoundary interactor;

    public ReadRuleController(ReadRuleInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void readRule() {
        ReadRuleInputData inputData = new ReadRuleInputData();
        interactor.execute(inputData);
    }
}
