package use_case.playerSplit;

public interface PlayerSplitOutputBoundary {
    void present(PlayerSplitOutputData outputData);
    void presentFailView(String message);
}
