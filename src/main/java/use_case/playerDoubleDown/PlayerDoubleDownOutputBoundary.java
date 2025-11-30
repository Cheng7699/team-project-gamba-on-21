package use_case.playerDoubleDown;


public interface PlayerDoubleDownOutputBoundary {
    void present(PlayerDoubleDownOutputData outputData);
    void presentFailView(String errorMessage);
}

