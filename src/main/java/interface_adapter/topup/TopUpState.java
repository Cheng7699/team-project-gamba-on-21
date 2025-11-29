package interface_adapter.topup;

public class TopUpState {
    private String username = "";
    private String usernameError;
    private String topupAmount = "";
    private String topupAmountError;

    public String getUsername() {
        return username;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public String getTopupAmount() {
        return topupAmount;
    }

    public String getTopupAmountError() {
        return topupAmountError;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public void setTopupAmount(String topupAmount) {
        this.topupAmount = topupAmount;
    }

    public void setTopupAmountError(String topupAmountError) {
        this.topupAmountError = topupAmountError;
    }

    @Override
    public String toString() {
        return "TopupState{"
                + "username='" + username + '\''
                + ", topupAmount='" + topupAmount + '\''
                + '}';
    }
}
