package entity;

public class Accounts {
    private final String username;
    private String password;
    private int balanceCents;   // 余额（分）
    private int selfLimitsCents; // 自我限制（单注或单日上限等：分）

    public Accounts(String username, String password, int balanceCents, int selfLimitsCents) {
        if (username == null || username.isEmpty()) throw new IllegalArgumentException("username required");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("password required");
        if (balanceCents < 0 || selfLimitsCents < 0) throw new IllegalArgumentException("negative money not allowed");
        this.username = username;
        this.password = password;
        this.balanceCents = balanceCents;
        this.selfLimitsCents = selfLimitsCents;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; } // 仅示例，生产中不要直接暴露
    public int getBalanceCents() { return balanceCents; }
    public int getSelfLimitsCents() { return selfLimitsCents; }

    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) throw new IllegalArgumentException("password required");
        this.password = newPassword;
    }
    public void setSelfLimitsCents(int cents) {
        if (cents < 0) throw new IllegalArgumentException("negative not allowed");
        this.selfLimitsCents = cents;
    }

    public boolean canAfford(int betCents) { return betCents >= 0 && balanceCents >= betCents; }
    public void debit(int cents) {
        if (cents < 0) throw new IllegalArgumentException("negative not allowed");
        if (cents > balanceCents) throw new IllegalStateException("insufficient funds");
        balanceCents -= cents;
    }
    public void credit(int cents) {
        if (cents < 0) throw new IllegalArgumentException("negative not allowed");
        balanceCents += cents;
    }
}