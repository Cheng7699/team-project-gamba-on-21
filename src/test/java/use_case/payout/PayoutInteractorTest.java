package use_case.payout;

import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayoutInteractorTest {
    private PayoutInteractor interactor;
    private MockPayoutUserDataAccess mockDataAccess;
    private MockPayoutPresenter mockPresenter;
    private BlackjackGame game;
    private BlackjackPlayer player;
    private BlackjackDealer dealer;
    private Accounts account;

    @BeforeEach
    void setUp() {
        mockDataAccess = new MockPayoutUserDataAccess();
        mockPresenter = new MockPayoutPresenter();
        interactor = new PayoutInteractor(mockDataAccess, mockPresenter);
        
        dealer = new BlackjackDealer();
        player = new BlackjackPlayer("testuser");
        game = new BlackjackGame("deck123", dealer, player);
        game.setBetAmount(100);
        
        account = new Accounts("testuser", "password", 800);
        mockDataAccess.save(account);
        mockDataAccess.setCurrentUsername("testuser");
    }

    @Test
    void testExecute_AccountNotFound() {
        mockDataAccess.setCurrentUsername("nonexistent");
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertFalse(mockPresenter.successViewCalled);
        assertNull(mockPresenter.outputData);
    }

    @Test
    void testExecute_PlayerWin_Regular() {
        setupHands(20, 18);
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(1000, mockPresenter.outputData.getNewBalance());
        assertEquals(100, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_PlayerWin_Blackjack() {
        setupBlackjackHands(true, false);
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(1050, mockPresenter.outputData.getNewBalance());
        assertEquals(150, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_PlayerLose() {
        setupHands(18, 20);
        game.playerLose();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(800, mockPresenter.outputData.getNewBalance());
        assertEquals(-100, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_Push() {
        setupHands(20, 20);
        game.push();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(900, mockPresenter.outputData.getNewBalance());
        assertEquals(0, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_Split_FirstWin_SecondWin() {
        setupSplitHands(20, 19, 18);
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(1200, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstWin_SecondLose() {
        setupSplitHands(20, 18, 19);
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerLose();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(900, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstWinBlackjack_SecondWin() {
        setupSplitHandsBlackjack(true, false, 19, 18);
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(1250, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstWin_SecondPush() {
        setupSplitHands(20, 20, 20);
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerPush();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(1100, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_DealerHandNull() {
        Hand playerHand = createHandWithTotal(20, "playerHand1");
        player.getHands().clear();
        player.addHand(playerHand);
        dealer.setHand(null);
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        interactor.execute(inputData);
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(1000, mockPresenter.outputData.getNewBalance());
    }

    private void setupHands(int playerTotal, int dealerTotal) {
        Hand playerHand = createHandWithTotal(playerTotal, "playerHand1");
        Hand dealerHand = createHandWithTotal(dealerTotal, "dealerHand");
        player.getHands().clear();
        player.addHand(playerHand);
        dealer.setHand(dealerHand);
    }

    private void setupSplitHands(int firstTotal, int secondTotal, int dealerTotal) {
        Hand firstHand = createHandWithTotal(firstTotal, "playerHand1");
        Hand secondHand = createHandWithTotal(secondTotal, "playerHand2");
        Hand dealerHand = createHandWithTotal(dealerTotal, "dealerHand");
        player.getHands().clear();
        player.addHand(firstHand);
        player.addHand(secondHand);
        dealer.setHand(dealerHand);
    }

    private void setupBlackjackHands(boolean playerHasBlackjack, boolean dealerHasBlackjack) {
        Hand playerHand;
        Hand dealerHand;
        
        if (playerHasBlackjack) {
            playerHand = new Hand("playerHand1");
            playerHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
            playerHand.addCard(new Card("KS", "url", "KING", "SPADES"));
        } else {
            playerHand = createHandWithTotal(20, "playerHand1");
        }
        
        if (dealerHasBlackjack) {
            dealerHand = new Hand("dealerHand");
            dealerHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
            dealerHand.addCard(new Card("QS", "url", "QUEEN", "SPADES"));
        } else {
            dealerHand = createHandWithTotal(18, "dealerHand");
        }
        
        player.getHands().clear();
        player.addHand(playerHand);
        dealer.setHand(dealerHand);
    }

    private void setupSplitHandsBlackjack(boolean firstHasBlackjack, boolean secondHasBlackjack,
                                         int otherFirstTotal, int otherSecondTotal) {
        Hand firstHand;
        Hand secondHand;
        Hand dealerHand = createHandWithTotal(18, "dealerHand");
        
        if (firstHasBlackjack) {
            firstHand = new Hand("playerHand1");
            firstHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
            firstHand.addCard(new Card("KS", "url", "KING", "SPADES"));
        } else {
            firstHand = createHandWithTotal(otherFirstTotal, "playerHand1");
        }
        
        if (secondHasBlackjack) {
            secondHand = new Hand("playerHand2");
            secondHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
            secondHand.addCard(new Card("QS", "url", "QUEEN", "SPADES"));
        } else {
            secondHand = createHandWithTotal(otherSecondTotal, "playerHand2");
        }
        
        player.getHands().clear();
        player.addHand(firstHand);
        player.addHand(secondHand);
        dealer.setHand(dealerHand);
    }

    private Hand createHandWithTotal(int total, String handID) {
        Hand hand = new Hand(handID);
        if (total == 21) {
            hand.addCard(new Card("AS", "url", "ACE", "SPADES"));
            hand.addCard(new Card("KS", "url", "KING", "SPADES"));
        } else if (total == 20) {
            hand.addCard(new Card("KS", "url", "KING", "SPADES"));
            hand.addCard(new Card("QS", "url", "QUEEN", "SPADES"));
        } else if (total == 19) {
            hand.addCard(new Card("KS", "url", "KING", "SPADES"));
            hand.addCard(new Card("9S", "url", "9", "SPADES"));
        } else if (total == 18) {
            hand.addCard(new Card("KS", "url", "KING", "SPADES"));
            hand.addCard(new Card("8S", "url", "8", "SPADES"));
        } else {
            hand.addCard(new Card("KS", "url", "KING", "SPADES"));
            hand.addCard(new Card((total - 10) + "S", "url", String.valueOf(total - 10), "SPADES"));
        }
        return hand;
    }

    private static class MockPayoutUserDataAccess implements PayoutUserDataAccessInterface {
        private Accounts account;
        private String currentUsername;

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

        void setCurrentUsername(String username) {
            this.currentUsername = username;
        }
    }

    private static class MockPayoutPresenter implements PayoutOutputBoundary {
        boolean successViewCalled = false;
        PayoutOutputData outputData = null;

        @Override
        public void prepareSuccessView(PayoutOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }
    }
}
