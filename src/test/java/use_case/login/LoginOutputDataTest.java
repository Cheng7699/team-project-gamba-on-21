package use_case.login;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class LoginOutputDataTest {

    @Test
    void testConstructorAndGetters_NormalCase() {
        LoginOutputData outputData = new LoginOutputData("testuser", 1000, 500);
        assertEquals("testuser", outputData.getUsername());
        assertEquals(1000, outputData.getBalance());
        assertEquals(500, outputData.getSelfLimit());
    }

    @Test
    void testConstructorAndGetters_ZeroValues() {
        LoginOutputData outputData = new LoginOutputData("testuser", 0, 0);
        assertEquals("testuser", outputData.getUsername());
        assertEquals(0, outputData.getBalance());
        assertEquals(0, outputData.getSelfLimit());
    }

    @Test
    void testConstructorAndGetters_NegativeBalance() {
        LoginOutputData outputData = new LoginOutputData("testuser", -100, 500);
        assertEquals("testuser", outputData.getUsername());
        assertEquals(-100, outputData.getBalance());
        assertEquals(500, outputData.getSelfLimit());
    }

    @Test
    void testConstructorAndGetters_LargeValues() {
        LoginOutputData outputData = new LoginOutputData("testuser", Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals("testuser", outputData.getUsername());
        assertEquals(Integer.MAX_VALUE, outputData.getBalance());
        assertEquals(Integer.MAX_VALUE, outputData.getSelfLimit());
    }

    @Test
    void testConstructorAndGetters_EmptyUsername() {
        LoginOutputData outputData = new LoginOutputData("", 1000, 500);
        assertEquals("", outputData.getUsername());
        assertEquals(1000, outputData.getBalance());
        assertEquals(500, outputData.getSelfLimit());
    }
}
