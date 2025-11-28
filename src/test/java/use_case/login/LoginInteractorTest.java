package use_case.login;

import entity.Accounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import data_access.InMemoryUserDataAccessObject;

import static org.junit.jupiter.api.Assertions.*;


class LoginInteractorTest {
    private LoginInteractor loginInteractor;
    private InMemoryUserDataAccessObject userDataAccessObject;
    private MockLoginPresenter loginPresenter;

    @BeforeEach
    void setUp() {
        userDataAccessObject = new InMemoryUserDataAccessObject();
        loginPresenter = new MockLoginPresenter();
        loginInteractor = new LoginInteractor(userDataAccessObject, loginPresenter);
    }

    @Test
    void testExecute_AccountDoesNotExist() {
        LoginInputData inputData = new LoginInputData("nonexistent", "password123");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.failViewCalled);
        assertEquals("nonexistent: Account does not exist.", loginPresenter.errorMessage);
        assertFalse(loginPresenter.successViewCalled);
        assertNull(loginPresenter.outputData);
    }

    @Test
    void testExecute_AccountDoesNotExist_EmptyUsername() {
        LoginInputData inputData = new LoginInputData("", "password123");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.failViewCalled);
        assertEquals(": Account does not exist.", loginPresenter.errorMessage);
    }

    @Test
    void testExecute_IncorrectPassword() {
        Accounts user = new Accounts("testuser", "correctpassword");
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "wrongpassword");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.failViewCalled);
        assertEquals("Incorrect password for \"testuser\".", loginPresenter.errorMessage);
        assertFalse(loginPresenter.successViewCalled);
        assertNull(loginPresenter.outputData);
        assertNull(userDataAccessObject.getCurrentUsername());
    }

    @Test
    void testExecute_IncorrectPassword_CaseSensitive() {
        Accounts user = new Accounts("testuser", "Password123");
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "password123");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.failViewCalled);
        assertEquals("Incorrect password for \"testuser\".", loginPresenter.errorMessage);
    }

    @Test
    void testExecute_IncorrectPassword_EmptyPassword() {
        Accounts user = new Accounts("testuser", "correctpassword");
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.failViewCalled);
        assertEquals("Incorrect password for \"testuser\".", loginPresenter.errorMessage);
    }

    @Test
    void testExecute_SuccessfulLogin() {
        Accounts user = new Accounts("testuser", "correctpassword", 500);
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "correctpassword");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.successViewCalled);
        assertFalse(loginPresenter.failViewCalled);
        assertNotNull(loginPresenter.outputData);
        assertEquals("testuser", loginPresenter.outputData.getUsername());
        assertEquals(500, loginPresenter.outputData.getBalance());
        assertEquals(1000, loginPresenter.outputData.getSelfLimit());
        assertEquals("testuser", userDataAccessObject.getCurrentUsername());
    }

    @Test
    void testExecute_SuccessfulLogin_DefaultBalance() {
        Accounts user = new Accounts("testuser", "correctpassword");
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "correctpassword");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.successViewCalled);
        assertEquals("testuser", loginPresenter.outputData.getUsername());
        assertEquals(1000, loginPresenter.outputData.getBalance());
        assertEquals(1000, loginPresenter.outputData.getSelfLimit());
        assertEquals("testuser", userDataAccessObject.getCurrentUsername());
    }

    @Test
    void testExecute_SuccessfulLogin_ZeroBalance() {
        Accounts user = new Accounts("testuser", "correctpassword", 0);
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "correctpassword");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.successViewCalled);
        assertEquals(0, loginPresenter.outputData.getBalance());
    }

    @Test
    void testExecute_SuccessfulLogin_SpecialCharactersInPassword() {
        Accounts user = new Accounts("testuser", "p@ssw0rd!@#$%");
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "p@ssw0rd!@#$%");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.successViewCalled);
        assertEquals("testuser", loginPresenter.outputData.getUsername());
    }

    @Test
    void testExecute_SuccessfulLogin_WhitespaceInPassword() {
        Accounts user = new Accounts("testuser", "password with spaces");
        userDataAccessObject.save(user);
        LoginInputData inputData = new LoginInputData("testuser", "password with spaces");
        loginInteractor.execute(inputData);
        assertTrue(loginPresenter.successViewCalled);
        assertEquals("testuser", loginPresenter.outputData.getUsername());
    }

    private static class MockLoginPresenter implements LoginOutputBoundary {
        boolean successViewCalled = false;
        boolean failViewCalled = false;
        LoginOutputData outputData = null;
        String errorMessage = null;

        @Override
        public void prepareSuccessView(LoginOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failViewCalled = true;
            this.errorMessage = errorMessage;
        }
    }
}
