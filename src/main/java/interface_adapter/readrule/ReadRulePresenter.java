package interface_adapter.readrule;

import use_case.readRule.ReadRuleOutputBoundary;
import use_case.readRule.ReadRuleOutputData;
import view.RulesView;

public class ReadRulePresenter implements ReadRuleOutputBoundary {

    private final RulesView view;

    public ReadRulePresenter(RulesView view) {
        this.view = view;
    }

    @Override
    public void present(ReadRuleOutputData outputData) {
        view.setRuleName(outputData.getText());
    }

    @Override
    public void presentFailView(String message) {
        System.out.println(message);
    }


}
