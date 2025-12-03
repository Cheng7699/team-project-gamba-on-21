package use_case.placeBet;

import entity.Accounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceBetInteractorTest {
    private PlaceBetInteractor interactor;
    private MockPlaceBetUserDataAccess mockDataAccess;
    private MockPlaceBetPresenter mockPresenter;
    private Accounts account;

    @BeforeEach
    void setUp() {
        mockDataAccess = new MockPlaceBetUserDataAccess();
        mockPresenter = new MockPlaceBetPresenter();
        interactor = new PlaceBetInteractor(mockDataAccess, mockPresenter);
        
        account = new Accounts("testuser", "password", 1000);
        mockDataAccess.save(account);
        mockDataAccess.setCurrentUsername("testuser");
    }


    @Test
    void testExecute_Success() {
        int betAmount = 100;
        int initialBalance = account.getBalance();
        
        interactor.execute(betAmount);
        
        assertTrue(mockPresenter.successViewCalled);
        assertNotNull(mockPresenter.outputData);
        assertEquals(initialBalance - betAmount, mockPresenter.outputData.getNewBalance());
        assertEquals(initialBalance - betAmount, account.getBalance());
    }

    @Test
    void testExecute_Success_ZeroBet() {
        int betAmount = 0;
        int initialBalance = account.getBalance();
        
        interactor.execute(betAmount);
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(initialBalance, mockPresenter.outputData.getNewBalance());
        assertEquals(initialBalance, account.getBalance());
    }

    @Test
    void testExecute_Success_LargeBet() {
        int betAmount = 500;
        int initialBalance = account.getBalance();
        
        interactor.execute(betAmount);
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(initialBalance - betAmount, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Success_AllBalance() {
        int betAmount = account.getBalance();
        
        interactor.execute(betAmount);
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(0, mockPresenter.outputData.getNewBalance());
        assertEquals(0, account.getBalance());
    }

    @Test
    void testExecute_AccountIsNull() {
        mockDataAccess.setAccountToReturn(null);
        
        interactor.execute(100);
        
        assertFalse(mockPresenter.successViewCalled);
        assertNull(mockPresenter.outputData);
    }

    @Test
    void testExecute_MultipleBets() {
        interactor.execute(100);
        assertEquals(900, account.getBalance());
        
        interactor.execute(200);
        assertEquals(700, account.getBalance());
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(700, mockPresenter.outputData.getNewBalance());
    }

    // Mock classes
    private static class MockPlaceBetUserDataAccess implements PlaceBetUserDataAccessInterface {
        private Accounts account;
        private String currentUsername;
        private Accounts accountToReturn;
        private boolean accountToReturnSet = false;

        @Override
        public Accounts get(String username) {
            if (accountToReturnSet) {
                return accountToReturn;
            }
            if (username.equals(currentUsername) && account != null) {
                return account;
            }
            return null;
        }

        @Override
        public void save(Accounts account) {
            this.account = account;
        }

        @Override
        public String getCurrentUsername() {
            return currentUsername;
        }

        void setCurrentUsername(String username) {
            this.currentUsername = username;
        }

        void setAccountToReturn(Accounts account) {
            this.accountToReturn = account;
            this.accountToReturnSet = true;
        }
    }

    private static class MockPlaceBetPresenter implements PlaceBetOutputBoundary {
        boolean successViewCalled = false;
        PlaceBetOutputData outputData = null;

        @Override
        public void prepareSuccessView(PlaceBetOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }
    }
}

