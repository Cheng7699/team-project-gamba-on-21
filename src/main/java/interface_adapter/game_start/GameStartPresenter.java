package interface_adapter.game_start;

import entity.BlackjackGame;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.game_start.GameStartOutputBoundary;
import use_case.game_start.GameStartOutputData;
import view.BlackjackView;

public class GameStartPresenter implements GameStartOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    public GameStartPresenter(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void present(GameStartOutputData outputData) {
        final LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setGame(outputData.getGame());
        this.loggedInViewModel.setState(loggedInState);
        this.loggedInViewModel.firePropertyChange();

    }

    public void presentFailView(String message) {
        System.out.print(message);
    }
}
