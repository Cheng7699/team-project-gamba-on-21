package use_case.readRule;

/**
 * The output boundary of the read rule use case
 */
public interface ReadRuleOutputBoundary {
    void present(ReadRuleOutputData outputData);
    void presentFailView(String message);
}
