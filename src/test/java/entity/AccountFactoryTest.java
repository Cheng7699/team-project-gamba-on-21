package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class AccountFactoryTest {
    private AccountFactory accountFactory;

    @BeforeEach
    void setUp() {
        accountFactory = new AccountFactory();
    }

    @Test
    void testCreate_TwoParameters_NormalCase() {
        Accounts account = accountFactory.create("testuser", "testpassword");
        assertNotNull(account);
        assertEquals("testuser", account.getUsername());
        assertEquals("testpassword", account.getPassword());
        assertEquals(1000, account.getBalance()); // Default balance
        assertEquals(1000, account.getSelfLimits()); // Default self limit
    }

    @Test
    void testCreate_TwoParameters_EmptyStrings() {
        Accounts account = accountFactory.create("", "");
        assertEquals("", account.getUsername());
        assertEquals("", account.getPassword());
        assertEquals(1000, account.getBalance());
    }

    @Test
    void testCreate_TwoParameters_SpecialCharacters() {
        Accounts account = accountFactory.create("user@name", "p@ssw0rd!@#$");
        assertEquals("user@name", account.getUsername());
        assertEquals("p@ssw0rd!@#$", account.getPassword());
        assertEquals(1000, account.getBalance());
    }

    @Test
    void testCreate_TwoParameters_Whitespace() {
        Accounts account = accountFactory.create("user name", "pass word");
        assertEquals("user name", account.getUsername());
        assertEquals("pass word", account.getPassword());
        assertEquals(1000, account.getBalance());
    }

    @Test
    void testCreate_TwoParameters_LongStrings() {
        String longUsername = "a".repeat(1000);
        String longPassword = "b".repeat(1000);
        Accounts account = accountFactory.create(longUsername, longPassword);
        assertEquals(longUsername, account.getUsername());
        assertEquals(longPassword, account.getPassword());
        assertEquals(1000, account.getBalance());
    }

    @Test
    void testCreate_ThreeParameters_NormalCase() {
        Accounts account = accountFactory.create("testuser", "testpassword", 2500);
        assertNotNull(account);
        assertEquals("testuser", account.getUsername());
        assertEquals("testpassword", account.getPassword());
        assertEquals(2500, account.getBalance());
        assertEquals(1000, account.getSelfLimits());
    }

    @Test
    void testCreate_ThreeParameters_ZeroBalance() {
        Accounts account = accountFactory.create("testuser", "testpassword", 0);
        assertEquals("testuser", account.getUsername());
        assertEquals("testpassword", account.getPassword());
        assertEquals(0, account.getBalance());
        assertEquals(1000, account.getSelfLimits());
    }

    @Test
    void testCreate_ThreeParameters_NegativeBalance() {
        Accounts account = accountFactory.create("testuser", "testpassword", -100);
        assertEquals("testuser", account.getUsername());
        assertEquals("testpassword", account.getPassword());
        assertEquals(-100, account.getBalance());
        assertEquals(1000, account.getSelfLimits());
    }

    @Test
    void testCreate_ThreeParameters_LargeBalance() {
        Accounts account = accountFactory.create("testuser", "testpassword", Integer.MAX_VALUE);
        assertEquals("testuser", account.getUsername());
        assertEquals("testpassword", account.getPassword());
        assertEquals(Integer.MAX_VALUE, account.getBalance());
        assertEquals(1000, account.getSelfLimits());
    }

    @Test
    void testCreate_ThreeParameters_EmptyStrings() {
        Accounts account = accountFactory.create("", "", 500);
        assertEquals("", account.getUsername());
        assertEquals("", account.getPassword());
        assertEquals(500, account.getBalance());
    }

    @Test
    void testCreate_ThreeParameters_SpecialCharacters() {
        Accounts account = accountFactory.create("user@name", "p@ssw0rd!@#$", 1500);
        assertEquals("user@name", account.getUsername());
        assertEquals("p@ssw0rd!@#$", account.getPassword());
        assertEquals(1500, account.getBalance());
    }

    @Test
    void testCreate_MultipleInstances_DifferentObjects() {
        Accounts account1 = accountFactory.create("user1", "pass1");
        Accounts account2 = accountFactory.create("user2", "pass2");
        assertNotSame(account1, account2);
        assertEquals("user1", account1.getUsername());
        assertEquals("user2", account2.getUsername());
    }

    @Test
    void testCreate_TwoParameters_DefaultBalanceConsistency() {
        Accounts account1 = accountFactory.create("user1", "pass1");
        Accounts account2 = accountFactory.create("user2", "pass2");
        assertEquals(1000, account1.getBalance());
        assertEquals(1000, account2.getBalance());
    }

    @Test
    void testCreate_ThreeParameters_CustomBalancePreserved() {
        Accounts account = accountFactory.create("testuser", "testpassword", 750);
        assertEquals(750, account.getBalance());
        account = accountFactory.create("anotheruser", "anotherpass", 2000);
        assertEquals(2000, account.getBalance());
    }
}

