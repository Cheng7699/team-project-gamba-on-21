package use_case.logout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import data_access.InMemoryUserDataAccessObject;

import static org.junit.jupiter.api.Assertions.*;


class LogoutInteractorTest {
    private LogoutInteractor logoutInteractor;
    private InMemoryUserDataAccessObject userDataAccessObject;
    private MockLogoutPresenter logoutPresenter;

    @BeforeEach
    void setUp() {
        userDataAccessObject = new InMemoryUserDataAccessObject();
        logoutPresenter = new MockLogoutPresenter();
        logoutInteractor = new LogoutInteractor(userDataAccessObject, logoutPresenter);
    }

    @Test
    void testExecute_WithCurrentUser() {
        // Arrange
        String username = "testuser";
        userDataAccessObject.setCurrentUsername(username);

        // Act
        logoutInteractor.execute();

        // Assert
        assertTrue(logoutPresenter.successViewCalled);
        assertNotNull(logoutPresenter.outputData);
        assertEquals(username, logoutPresenter.outputData.getUsername());
        assertNull(userDataAccessObject.getCurrentUsername());
    }

    @Test
    void testExecute_NoCurrentUser() {
        // Arrange
        userDataAccessObject.setCurrentUsername(null);

        // Act
        logoutInteractor.execute();

        // Assert
        assertTrue(logoutPresenter.successViewCalled);
        assertNotNull(logoutPresenter.outputData);
        assertNull(logoutPresenter.outputData.getUsername());
        assertNull(userDataAccessObject.getCurrentUsername());
    }

    @Test
    void testExecute_EmptyStringUsername() {
        // Arrange
        userDataAccessObject.setCurrentUsername("");

        // Act
        logoutInteractor.execute();

        // Assert
        assertTrue(logoutPresenter.successViewCalled);
        assertNotNull(logoutPresenter.outputData);
        assertEquals("", logoutPresenter.outputData.getUsername());
        assertNull(userDataAccessObject.getCurrentUsername());
    }

    @Test
    void testExecute_MultipleLogouts() {
        // Arrange
        userDataAccessObject.setCurrentUsername("user1");
        logoutInteractor.execute();
        logoutPresenter.successViewCalled = false; // Reset flag

        // Act - logout again when already logged out
        logoutInteractor.execute();

        // Assert
        assertTrue(logoutPresenter.successViewCalled);
        assertNull(logoutPresenter.outputData.getUsername());
        assertNull(userDataAccessObject.getCurrentUsername());
    }

    /**
     * Mock implementation of LogoutOutputBoundary for testing.
     */
    private static class MockLogoutPresenter implements LogoutOutputBoundary {
        boolean successViewCalled = false;
        LogoutOutputData outputData = null;

        @Override
        public void prepareSuccessView(LogoutOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }
    }
}
