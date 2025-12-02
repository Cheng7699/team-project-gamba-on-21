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
        
        account = new Accounts("testuser", "password", 800); // Balance after bet deduction
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
        setupHands(20, 18); // Player 20, Dealer 18
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        assertNotNull(mockPresenter.outputData);
        assertEquals(1000, mockPresenter.outputData.getNewBalance()); // 800 + 100 (stake) + 100 (winnings)
        assertEquals(100, mockPresenter.outputData.getPayoutAmount());
        assertEquals("PlayerWin", mockPresenter.outputData.getResult());
    }

    @Test
    void testExecute_PlayerWin_Blackjack() {
        setupBlackjackHands(true, false); // Player has blackjack, dealer doesn't
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // Blackjack pays 3:2, so 100 * 3 / 2 = 150 winnings
        // Balance: 800 + 100 (stake) + 150 (winnings) = 1050
        assertEquals(1050, mockPresenter.outputData.getNewBalance());
        assertEquals(150, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_PlayerLose() {
        setupHands(18, 20); // Player 18, Dealer 20
        game.playerLose();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(800, mockPresenter.outputData.getNewBalance()); // No change, bet already deducted
        assertEquals(-100, mockPresenter.outputData.getPayoutAmount());
        assertEquals("PlayerLose", mockPresenter.outputData.getResult());
    }

    @Test
    void testExecute_Push() {
        setupHands(20, 20); // Both have 20
        game.push();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(900, mockPresenter.outputData.getNewBalance()); // 800 + 100 (stake returned)
        assertEquals(0, mockPresenter.outputData.getPayoutAmount());
        assertEquals("Push", mockPresenter.outputData.getResult());
    }

    @Test
    void testExecute_UnknownResult() {
        setupHands(20, 18);
        game.setResult("Unknown");
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(800, mockPresenter.outputData.getNewBalance()); // No change
        assertEquals(0, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_Split_FirstHandWin_SecondHandWin() {
        setupSplitHands(20, 19, 18); // First 20, Second 19, Dealer 18
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 + 100 + 100 = 1000
        // Second hand: 1000 + 100 + 100 = 1200
        assertEquals(1200, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstHandWin_SecondHandLose() {
        setupSplitHands(20, 18, 19); // First 20, Second 18, Dealer 19
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerLose();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 + 100 + 100 = 1000
        // Second hand: 1000 - 100 = 900 (no change to balance)
        assertEquals(900, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstHandLose_SecondHandWin() {
        setupSplitHands(18, 20, 19); // First 18, Second 20, Dealer 19
        game.setSplitted(true);
        game.playerLose();
        game.splitPlayerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 (no change)
        // Second hand: 800 + 100 + 100 = 1000
        assertEquals(1000, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstHandLose_SecondHandLose() {
        setupSplitHands(18, 17, 19); // First 18, Second 17, Dealer 19
        game.setSplitted(true);
        game.playerLose();
        game.splitPlayerLose();
        PayoutInputData inputData = new PayoutInputData(game);

        interactor.execute(inputData);

        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 (no change)
        // Second hand: 800 (no change)
        assertEquals(800, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstHandPush_SecondHandPush() {
        setupSplitHands(20, 20, 20); // All have 20
        game.setSplitted(true);
        game.push();
        game.splitPlayerPush();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 + 100 = 900
        // Second hand: 900 + 100 = 1000
        assertEquals(1000, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstHandWin_Blackjack_SecondHandWin() {
        setupSplitHandsBlackjack(true, false, 19, 18); // First blackjack, Second 19, Dealer 18
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 + 100 + 150 = 1050
        // Second hand: 1050 + 100 + 100 = 1250
        assertEquals(1250, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_FirstHandWin_SecondHandWin_Blackjack() {
        setupSplitHandsBlackjack(false, true, 20, 18); // First 20, Second blackjack, Dealer 18
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 + 100 + 100 = 1000
        // Second hand: 1000 + 100 + 150 = 1250
        assertEquals(1250, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_Split_SecondHandUnknown() {
        setupSplitHands(20, 19, 18);
        game.setSplitted(true);
        game.playerWin();
        // Second result remains empty string for unknown (default value)
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 + 100 + 100 = 1000
        // Second hand: 1000 (no change for unknown/empty result)
        assertEquals(1000, mockPresenter.outputData.getNewBalance());
    }

    @Test
    void testExecute_DoubleDownBet_PlayerWin() {
        game.setBetAmount(200); // Doubled bet
        setupHands(20, 18);
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // 800 + 200 (stake) + 200 (winnings) = 1200
        assertEquals(1200, mockPresenter.outputData.getNewBalance());
        assertEquals(200, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_DoubleDownBet_PlayerLose() {
        game.setBetAmount(200); // Doubled bet
        setupHands(18, 20);
        game.playerLose();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // 800 (no change, bet already deducted)
        assertEquals(800, mockPresenter.outputData.getNewBalance());
        assertEquals(-200, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_PlayerHasBlackjack_DealerHasBlackjack() {
        setupBlackjackHands(true, true); // Both have blackjack
        game.push(); // Push when both have blackjack
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        assertEquals(900, mockPresenter.outputData.getNewBalance()); // Stake returned
    }

    @Test
    void testExecute_PlayerNotBlackjack_ThreeCards() {
        // Player has 21 but with 3 cards (not blackjack)
        Hand playerHand = new Hand("playerHand1");
        playerHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
        playerHand.addCard(new Card("2S", "url", "2", "SPADES"));
        playerHand.addCard(new Card("8S", "url", "8", "SPADES")); // Total 21 with 3 cards
        player.getHands().clear();
        player.addHand(playerHand);
        
        Hand dealerHand = new Hand("dealerHand");
        dealerHand.addCard(new Card("KS", "url", "KING", "SPADES"));
        dealerHand.addCard(new Card("QS", "url", "QUEEN", "SPADES"));
        dealer.setHand(dealerHand);
        
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // Should be regular win (1:1), not blackjack (3:2)
        assertEquals(1000, mockPresenter.outputData.getNewBalance()); // 800 + 100 + 100
        assertEquals(100, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_PlayerNotBlackjack_TwentyWithTwoCards() {
        // Player has 20 with 2 cards (not blackjack, regular win)
        setupHands(20, 18);
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // Should be regular win (1:1), not blackjack
        assertEquals(1000, mockPresenter.outputData.getNewBalance());
        assertEquals(100, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_DealerNotBlackjack_ThreeCards() {
        // Dealer has 21 but with 3 cards (not blackjack)
        Hand playerHand = new Hand("playerHand1");
        playerHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
        playerHand.addCard(new Card("KS", "url", "KING", "SPADES"));
        player.getHands().clear();
        player.addHand(playerHand);
        
        Hand dealerHand = new Hand("dealerHand");
        dealerHand.addCard(new Card("AS", "url", "ACE", "SPADES"));
        dealerHand.addCard(new Card("2S", "url", "2", "SPADES"));
        dealerHand.addCard(new Card("8S", "url", "8", "SPADES")); // Total 21 with 3 cards
        dealer.setHand(dealerHand);
        
        game.playerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // Player has blackjack, dealer doesn't (even though dealer has 21 with 3 cards)
        assertEquals(1050, mockPresenter.outputData.getNewBalance()); // 800 + 100 + 150
        assertEquals(150, mockPresenter.outputData.getPayoutAmount());
    }

    @Test
    void testExecute_Split_FirstHandWin_SecondHandWin_Regular() {
        // Both hands win but neither has blackjack
        setupSplitHands(20, 19, 18);
        game.setSplitted(true);
        game.playerWin();
        game.splitPlayerWin();
        PayoutInputData inputData = new PayoutInputData(game);
        
        interactor.execute(inputData);
        
        assertTrue(mockPresenter.successViewCalled);
        // First hand: 800 + 100 + 100 = 1000
        // Second hand: 1000 + 100 + 100 = 1200
        assertEquals(1200, mockPresenter.outputData.getNewBalance());
    }

    // Helper methods
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
        } else if (total == 17) {
            hand.addCard(new Card("KS", "url", "KING", "SPADES"));
            hand.addCard(new Card("7S", "url", "7", "SPADES"));
        } else {
            // Default: use two cards that sum to total
            int firstCard = Math.min(10, total - 7);
            int secondCard = total - firstCard;
            hand.addCard(new Card(firstCard + "S", "url", String.valueOf(firstCard), "SPADES"));
            hand.addCard(new Card(secondCard + "S", "url", String.valueOf(secondCard), "SPADES"));
        }
        return hand;
    }

    // Mock classes
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

