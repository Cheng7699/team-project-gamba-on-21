package entity;

/**
 * Factory for creating CommonUser objects.
 */
public class AccountFactory {

    public Accounts create(String name, String password) {return new Accounts(name, password);}
}
