package use_case.game_start;


public interface GameStartOutputBoundary {
    void present(GameStartOutputData outputData);
    void presentFailView(String message);
}
