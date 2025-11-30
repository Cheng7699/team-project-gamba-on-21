package use_case.placeBet;

/**
 * Output data for place bet use case.
 */
public class PlaceBetOutputData {
    private final int newBalance;

    public PlaceBetOutputData(int newBalance) {
        this.newBalance = newBalance;
    }

    public int getNewBalance() {
        return newBalance;
    }
}

