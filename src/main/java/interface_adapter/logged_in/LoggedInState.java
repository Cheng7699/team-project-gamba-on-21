package interface_adapter.logged_in;

import entity.BlackjackGame;

/**
 * The State information representing the logged-in user.
 */
public class LoggedInState {
    private String username = "";

    private String password = "";
    private String passwordError;
    private String gameState;

    private int balance;
    private int selfLimit;
    private String statusMessage = "";
    private BlackjackGame blackjackGame;

    public LoggedInState(LoggedInState copy) {
        username = copy.username;
        password = copy.password;
        passwordError = copy.passwordError;
        balance = copy.balance;
        selfLimit = copy.selfLimit;
        statusMessage = copy.statusMessage;
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public LoggedInState() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getGameState() { return gameState; }

    public void setGame(BlackjackGame blackjackGame) { this.blackjackGame = blackjackGame; }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getSelfLimit() {
        return selfLimit;
    }

    public void setSelfLimit(int selfLimit) {
        this.selfLimit = selfLimit;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public BlackjackGame getGame() { return this.blackjackGame; }
}
