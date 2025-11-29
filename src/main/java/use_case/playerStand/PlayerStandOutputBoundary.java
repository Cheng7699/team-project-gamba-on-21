package use_case.playerStand;

public interface PlayerStandOutputBoundary {
    void present(PlayerStandOutputData outputData);

    void presentFailView(String message);
}
