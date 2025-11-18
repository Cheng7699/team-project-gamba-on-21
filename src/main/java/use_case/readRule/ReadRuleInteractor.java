package use_case.readRule;

public class ReadRuleInteractor implements ReadRuleInputBoundary {
    private final ReadRuleOutputBoundary readRulePresenter;

    public ReadRuleInteractor(ReadRuleOutputBoundary readRulePresenter) {
        this.readRulePresenter = readRulePresenter;
    }

    @Override
    public void execute(ReadRuleInputData readRuleInputData) {

    }
}
