package use_case.playerDoubleDown;

import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDoubleDownInteractorTest {
    private PlayerDoubleDownInteractor interactor;
    private MockPlayerDoubleDownUserDataAccess mockDataAccess;
    private MockPlayerDoubleDownPresenter mockPresenter;
    private BlackjackGame game;
    private BlackjackPlayer player;
    private BlackjackDealer dealer;
    private Accounts account;

    @BeforeEach
    void setUp() {
        mockDataAccess = new MockPlayerDoubleDownUserDataAccess();
        mockPresenter = new MockPlayerDoubleDownPresenter();
        interactor = new PlayerDoubleDownInteractor(mockDataAccess, mockPresenter);
        
        dealer = new BlackjackDealer();
        player = new BlackjackPlayer("testuser");
        game = new BlackjackGame("deck123", dealer, player);
        game.setBetAmount(100);
        game.setResult("InGame");
        
        account = new Accounts("testuser", "password", 1000);
        mockDataAccess.save(account);
        mockDataAccess.setCurrentUsername("testuser");
    }

    @Test
    void testExecute_HandValidation_NullOrEmpty() {
        player.getHands().clear();
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.failViewCalled);
        assertEquals("No hand available to double down.", mockPresenter.errorMessage);
    }

    @Test
    void testExecute_HandValidation_WrongCardCount() {
        Hand hand = new Hand("hand1");
        hand.addCard(new Card("AS", "url", "ACE", "SPADES"));
        player.addHand(hand);
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.failViewCalled);
        assertTrue(mockPresenter.errorMessage.contains("exactly 2 cards"));
    }

    @Test
    void testExecute_HandValidation_Bust() {
        Hand fakeBustHand = new Hand("fake") {
            @Override
            public boolean isBust() {
                return true;
            }
        };
        fakeBustHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
        fakeBustHand.addCard(new Card("2S", "url", "2", "SPADES"));
        player.addHand(fakeBustHand);
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.failViewCalled);
        assertEquals("Cannot double down on a bust hand.", mockPresenter.errorMessage);
    }

    @Test
    void testExecute_GameStateValidation_GameOver() {
        Hand hand = createValidHand();
        player.addHand(hand);
        game.setResult("PlayerWin");
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.failViewCalled);
        assertEquals("Cannot double down after game is over.", mockPresenter.errorMessage);
    }


    @Test
    void testExecute_InsufficientBalance() {
        Hand hand = createValidHand();
        player.addHand(hand);
        account.setBalance(50);
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.failViewCalled);
        assertTrue(mockPresenter.errorMessage.contains("Insufficient funds"));
    }

    @Test
    void testExecute_Success_NotBust() {
        Hand hand = createValidHand();
        player.addHand(hand);
        mockDataAccess.setCardToDraw(new Card("2S", "url", "2", "SPADES"));
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(200, mockPresenter.outputData.getNewBetAmount());
        assertEquals(900, mockPresenter.outputData.getNewBalance());
        assertFalse(mockPresenter.outputData.isBust());
    }

    @Test
    void testExecute_Success_Bust() {
        Hand hand = new Hand("hand1");
        hand.addCard(new Card("10S", "url", "10", "SPADES"));
        hand.addCard(new Card("10H", "url", "10", "HEARTS"));
        player.addHand(hand);
        mockDataAccess.setCardToDraw(new Card("KS", "url", "KING", "SPADES"));
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertTrue(mockPresenter.outputData.isBust());
        assertEquals("PlayerLose", game.getResult());
    }

    @Test
    void testExecute_Success_SplitHandBust() {
        Hand firstHand = createValidHand();
        Hand splitHand = new Hand("hand2");
        splitHand.addCard(new Card("10S", "url", "10", "SPADES"));
        splitHand.addCard(new Card("10H", "url", "10", "HEARTS"));
        player.addHand(firstHand);
        player.addHand(splitHand);
        game.setSplitted(true);
        mockDataAccess.setCardToDraw(new Card("KS", "url", "KING", "SPADES"));
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, true);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertTrue(mockPresenter.outputData.isBust());
        assertEquals("PlayerLose", game.getResult());
    }

    @Test
    void testExecute_Success_FirstHandBustInSplit() {
        Hand firstHand = new Hand("hand1");
        firstHand.addCard(new Card("10S", "url", "10", "SPADES"));
        firstHand.addCard(new Card("10H", "url", "10", "HEARTS"));
        Hand secondHand = createValidHand();
        player.addHand(firstHand);
        player.addHand(secondHand);
        game.setSplitted(true);
        String initialResult = game.getResult();
        mockDataAccess.setCardToDraw(new Card("KS", "url", "KING", "SPADES"));
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertTrue(mockPresenter.outputData.isBust());
        assertEquals(initialResult, game.getResult());
    }

    @Test
    void testExecute_CardDrawFailure_Rollback() {
        Hand hand = createValidHand();
        player.addHand(hand);
        mockDataAccess.setThrowExceptionOnDraw(true);
        int originalBalance = account.getBalance();
        int originalBet = (int) game.getBetAmount();
        PlayerDoubleDownInputData inputData = new PlayerDoubleDownInputData(game, player, false);
        interactor.execute(inputData);
        assertTrue(mockPresenter.failViewCalled);
        assertEquals("Problem drawing card for double down.", mockPresenter.errorMessage);
        assertEquals(originalBalance, account.getBalance());
        assertEquals(originalBet, (int) game.getBetAmount());
    }

    private Hand createValidHand() {
        Hand hand = new Hand("hand1");
        hand.addCard(new Card("AS", "url", "ACE", "SPADES"));
        hand.addCard(new Card("2S", "url", "2", "SPADES"));
        return hand;
    }

    private static class MockPlayerDoubleDownUserDataAccess
            implements PlayerDoubleDownUserDataAccessInterface {
        private Accounts account;
        private String currentUsername;
        private Card cardToDraw;
        private boolean throwExceptionOnDraw = false;

        @Override
        public Accounts get(String username) {
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

        @Override
        public Card drawCard() {
            if (throwExceptionOnDraw) {
                throw new RuntimeException("Card draw failed");
            }
            return cardToDraw;
        }

        void setCurrentUsername(String username) {
            this.currentUsername = username;
        }

        void setCardToDraw(Card card) {
            this.cardToDraw = card;
        }

        void setThrowExceptionOnDraw(boolean throwException) {
            this.throwExceptionOnDraw = throwException;
        }
    }

    private static class MockPlayerDoubleDownPresenter
            implements PlayerDoubleDownOutputBoundary {
        boolean successViewCalled = false;
        boolean failViewCalled = false;
        PlayerDoubleDownOutputData outputData = null;
        String errorMessage = null;

        @Override
        public void present(PlayerDoubleDownOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void presentFailView(String errorMessage) {
            this.failViewCalled = true;
            this.errorMessage = errorMessage;
        }
    }
}
