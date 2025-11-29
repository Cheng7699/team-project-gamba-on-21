package use_case.logout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class LogoutOutputDataTest {

    @Test
    void testConstructorAndGetter_NormalCase() {
        LogoutOutputData outputData = new LogoutOutputData("testuser");
        assertEquals("testuser", outputData.getUsername());
    }

    @Test
    void testConstructorAndGetter_EmptyString() {
        LogoutOutputData outputData = new LogoutOutputData("");
        assertEquals("", outputData.getUsername());
    }

    @Test
    void testConstructorAndGetter_Null() {
        LogoutOutputData outputData = new LogoutOutputData(null);
        assertNull(outputData.getUsername());
    }

    @Test
    void testConstructorAndGetter_LongString() {
        String longUsername = "a".repeat(1000);
        LogoutOutputData outputData = new LogoutOutputData(longUsername);
        assertEquals(longUsername, outputData.getUsername());
    }
}
