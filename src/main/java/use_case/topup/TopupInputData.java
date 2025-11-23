package use_case.topup;

public class TopupInputData {

    private final String username;
    private final String topupAmount;

    public TopupInputData(String username, String topupAmount) {
        this.username = username;
        this.topupAmount = topupAmount;
    }

    String getUsername() {return username;}

    String getTopupAmount() {return topupAmount;}
}
