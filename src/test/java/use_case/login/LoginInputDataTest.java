package use_case.login;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class LoginInputDataTest {

    @Test
    void testConstructorAndGetters_NormalCase() {
        LoginInputData inputData = new LoginInputData("testuser", "testpassword");
        assertEquals("testuser", inputData.getUsername());
        assertEquals("testpassword", inputData.getPassword());
    }

    @Test
    void testConstructorAndGetters_EmptyStrings() {
        LoginInputData inputData = new LoginInputData("", "");
        assertEquals("", inputData.getUsername());
        assertEquals("", inputData.getPassword());
    }

    @Test
    void testConstructorAndGetters_NullValues() {
        LoginInputData inputData = new LoginInputData(null, null);
        assertNull(inputData.getUsername());
        assertNull(inputData.getPassword());
    }

    @Test
    void testConstructorAndGetters_SpecialCharacters() {
        LoginInputData inputData = new LoginInputData("user@name", "p@ssw0rd!@#$");
        assertEquals("user@name", inputData.getUsername());
        assertEquals("p@ssw0rd!@#$", inputData.getPassword());
    }

    @Test
    void testConstructorAndGetters_Whitespace() {
        LoginInputData inputData = new LoginInputData("user name", "pass word");
        assertEquals("user name", inputData.getUsername());
        assertEquals("pass word", inputData.getPassword());
    }

    @Test
    void testConstructorAndGetters_LongStrings() {
        String longUsername = "a".repeat(1000);
        String longPassword = "b".repeat(1000);
        LoginInputData inputData = new LoginInputData(longUsername, longPassword);
        assertEquals(longUsername, inputData.getUsername());
        assertEquals(longPassword, inputData.getPassword());
    }
}
