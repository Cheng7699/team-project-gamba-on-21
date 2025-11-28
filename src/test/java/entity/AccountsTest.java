package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for Accounts entity.
 * tests ONLY the methods used in Login/Logout use cases:
 * - Constructors
 * - getUsername()
 * - getPassword()
 * - getBalance()
 * - getSelfLimits()
 */
class AccountsTest {

    // Constructor Tests

    @Test
    void testConstructor_TwoParameters() {
        Accounts account = new Accounts("username", "password");
        assertEquals("username", account.getUsername());
        assertEquals("password", account.getPassword());
        assertEquals(1000, account.getBalance()); // Default balance
        assertEquals(1000, account.getSelfLimits()); // Default self limit
    }

    @Test
    void testConstructor_ThreeParameters() {

        Accounts account = new Accounts("username", "password", 2500);
        assertEquals("username", account.getUsername());
        assertEquals("password", account.getPassword());
        assertEquals(2500, account.getBalance());
        assertEquals(1000, account.getSelfLimits()); // Default self limit
    }

    @Test
    void testConstructor_ThreeParameters_ZeroBalance() {
        Accounts account = new Accounts("username", "password", 0);
        assertEquals(0, account.getBalance());
    }

    @Test
    void testConstructor_ThreeParameters_NegativeBalance() {
        Accounts account = new Accounts("username", "password", -100);
        assertEquals(-100, account.getBalance());
    }

    // Getter Tests - Only for methods used in Login/Logout

    @Test
    void testGetUsername() {
        Accounts account = new Accounts("testuser", "password");
        String username = account.getUsername();
        assertEquals("testuser", username);
    }

    @Test
    void testGetPassword() {
        Accounts account = new Accounts("username", "testpassword");
        String password = account.getPassword();
        assertEquals("testpassword", password);
    }

    @Test
    void testGetBalance() {
        Accounts account = new Accounts("username", "password", 1500);
        Integer balance = account.getBalance();
        assertEquals(1500, balance);
    }

    @Test
    void testGetSelfLimits() {
        Accounts account = new Accounts("username", "password");
        Integer selfLimits = account.getSelfLimits();
        assertEquals(1000, selfLimits);
    }

    @Test
    void testGetUsername_EmptyString() {
        Accounts account = new Accounts("", "password");
        String username = account.getUsername();
        assertEquals("", username);
    }

    @Test
    void testGetPassword_EmptyString() {
        Accounts account = new Accounts("username", "");
        String password = account.getPassword();
        assertEquals("", password);
    }

    @Test
    void testGetBalance_Zero() {
        Accounts account = new Accounts("username", "password", 0);
        Integer balance = account.getBalance();
        assertEquals(0, balance);
    }

    @Test
    void testGetBalance_LargeValue() {
        Accounts account = new Accounts("username", "password", Integer.MAX_VALUE);
        Integer balance = account.getBalance();
        assertEquals(Integer.MAX_VALUE, balance);
    }

    @Test
    void testGetPassword_SpecialCharacters() {
        Accounts account = new Accounts("username", "p@ssw0rd!@#$%");
        String password = account.getPassword();
        assertEquals("p@ssw0rd!@#$%", password);
    }

    @Test
    void testGetUsername_SpecialCharacters() {
        Accounts account = new Accounts("user@name", "password");
        String username = account.getUsername();
        assertEquals("user@name", username);
    }
}
