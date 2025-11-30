package interface_adapter.placeBet;

import use_case.placeBet.PlaceBetInteractor;

/**
 * Controller for place bet use case.
 */
public class PlaceBetController {
    private final PlaceBetInteractor placeBetInteractor;

    public PlaceBetController(PlaceBetInteractor placeBetInteractor) {
        this.placeBetInteractor = placeBetInteractor;
    }

    public void execute(int betAmount) {
        placeBetInteractor.execute(betAmount);
    }
}

