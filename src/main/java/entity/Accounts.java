package entity;

public class Accounts {
    private final String username;
    private final String password;
    private Integer balance;
    private final Integer selfLimits;

    /**
     * I arbitrarily set selfLimits to 1000 because we hadn't decided the number.
     */
    public Accounts(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 0;
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
