package use_case.payout;

/**
 * Output data for payout use case.
 */
public class PayoutOutputData {
    private final int newBalance;
    private final int payoutAmount;
    private final String result;

    public PayoutOutputData(int newBalance, int payoutAmount, String result) {
        this.newBalance = newBalance;
        this.payoutAmount = payoutAmount;
        this.result = result;
    }

    public int getNewBalance() {
        return newBalance;
    }

    public int getPayoutAmount() {
        return payoutAmount;
    }

    public String getResult() {
        return result;
    }
}

