package use_case.login;

/**
 * Output Data for the Login Use Case.
 */
public class LoginOutputData {

    private final String username;
    private final int balance;
    private final int selfLimit;

    public LoginOutputData(String username, int balance, int selfLimit) {
        this.username = username;
        this.balance = balance;
        this.selfLimit = selfLimit;
    }

    public String getUsername() {
        return username;
    }

    public int getBalance() {
        return balance;
    }

    public int getSelfLimit() {
        return selfLimit;
    }

}
