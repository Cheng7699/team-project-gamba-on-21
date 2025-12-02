package use_case.topup;

import entity.AccountFactory;
import entity.Accounts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TopupInteractor.
 */
public class TopupInteractorTest {

    /**
     * in-memory DAO stub
     */
    private static class InMemoryTopupUserDataAccess implements TopupUserDataAccessInterface {
        private Accounts storedUser;      // user “in the database”
        private Accounts lastTopupUser;   // last user passed to topup(...)

        InMemoryTopupUserDataAccess(Accounts storedUser) {
            this.storedUser = storedUser;
        }

        @Override
        public void topup(Accounts user) {
            // Simulate persisting the new balance.
            this.lastTopupUser = user;
            this.storedUser = user;
        }

        @Override
        public Accounts get(String name) {
            if (storedUser != null && storedUser.getUsername().equals(name)) {
                return storedUser;
            }
            return null;
        }
    }

    /**
     * Presenter stub to capture success/failure output from the interactor.
     */
    private static class TestTopupPresenter implements TopupOutputBoundary {
        TopupOutputData lastSuccess;
        String lastError;

        @Override
        public void prepareSuccessView(TopupOutputData outputData) {
            this.lastSuccess = outputData;
        }

        @Override
        public void prepareFailureView(String errorMessage) {
            this.lastError = errorMessage;
        }
    }

    /**
     * Happy path: valid positive integer amount, user exists.
     */
    @Test
    public void testExecute_successfulTopup() {
        AccountFactory accountFactory = new AccountFactory();
        Accounts user = accountFactory.create("alice", "pw", 100);
        InMemoryTopupUserDataAccess dao = new InMemoryTopupUserDataAccess(user);
        TestTopupPresenter presenter = new TestTopupPresenter();
        TopupInteractor interactor = new TopupInteractor(dao, presenter);

        TopupInputData input = new TopupInputData("alice", "50");

        interactor.execute(input);

        assertNull(presenter.lastError, "No error should be reported on success");
        assertNotNull(presenter.lastSuccess, "Success output should be provided");

        assertEquals(150, user.getBalance().intValue());
        assertEquals(150, presenter.lastSuccess.getnewBalance());
        assertSame(user, dao.lastTopupUser, "DAO should have been called with the same user instance");

    }

    /**
     * Failure: amount <= 0 should not call DAO and should return proper message.
     */
    @Test
    public void testExecute_amountLessOrEqualZero() {
        // User won't be used because we exit before hitting DAO.get(...)
        InMemoryTopupUserDataAccess dao = new InMemoryTopupUserDataAccess(null);
        TestTopupPresenter presenter = new TestTopupPresenter();
        TopupInteractor interactor = new TopupInteractor(dao, presenter);

        TopupInputData inputZero = new TopupInputData("any", "0");

        interactor.execute(inputZero);

        assertEquals("Amount must be greater than 0", presenter.lastError);
        assertNull(presenter.lastSuccess, "Success output should not be created");
        assertNull(dao.lastTopupUser, "DAO.topup should not have been called");
    }

    /**
     * Failure: user not found in DAO.
     */
    @Test
    public void testExecute_userNotFound() {
        AccountFactory accountFactory = new AccountFactory();
        // DAO has a user with a different username, so get(...) returns null
        Accounts otherUser = accountFactory.create("bob", "pw", 200);
        InMemoryTopupUserDataAccess dao = new InMemoryTopupUserDataAccess(otherUser);
        TestTopupPresenter presenter = new TestTopupPresenter();
        TopupInteractor interactor = new TopupInteractor(dao, presenter);

        TopupInputData input = new TopupInputData("alice", "50");

        interactor.execute(input);

        assertEquals("User not found.", presenter.lastError);
        assertNull(presenter.lastSuccess,
                "Success output should not be created when user is missing");
        assertNull(dao.lastTopupUser,
                "DAO.topup should not be called when user is null");
    }

    @Test
    public void testExecute_invalidAmount_notAnInteger() {
        AccountFactory accountFactory = new AccountFactory();
        Accounts user = accountFactory.create("alice", "pw", 100);
        InMemoryTopupUserDataAccess dao = new InMemoryTopupUserDataAccess(user);
        TestTopupPresenter presenter = new TestTopupPresenter();
        TopupInteractor interactor = new TopupInteractor(dao, presenter);

        TopupInputData input = new TopupInputData("alice", "abc");

        interactor.execute(input);

        assertEquals("Please enter an integer", presenter.lastError);
        assertNull(presenter.lastSuccess,
                "Success output should not be created on invalid amount");
        assertEquals(100, user.getBalance().intValue(),
                "Balance should not change on invalid input");
        assertNull(dao.lastTopupUser,
                "DAO.topup should not have been called");

    }
}