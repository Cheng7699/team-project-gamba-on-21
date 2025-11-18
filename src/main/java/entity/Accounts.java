package entity;

public class Accounts {
    private final String username;
    private final String password;
    private Integer balance;
    private final Integer selfLimits;

    /**
     * selfLimits arbitrarily set to 1000 (Selflimit function Currently Unimplemented)
     * balance arbitrarily set to 1000 chips on new account creation
     */
    public Accounts(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 1000;
        this.selfLimits = 1000;
    }
    public Accounts(String username, String password, Integer balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.selfLimits = 1000;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Integer getBalance() { return balance; }
    public Integer getSelfLimits() { return selfLimits; }

    public void setBalance(Integer newBalance) { this.balance = newBalance; }

    public void addFunds(Integer amount) { this.balance += amount; }

    public void subtractFunds(Integer amount) { this.balance -= amount; }
}
