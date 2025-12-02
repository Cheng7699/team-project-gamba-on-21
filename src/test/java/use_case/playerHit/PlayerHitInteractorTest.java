package use_case.playerHit;

import entity.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerHitInteractorTest {

    private static class MockDeck implements PlayerHitUserDataAccessInterface {
        final private Card card;
        boolean drawCalled = false;

        MockDeck(Card card) {
            this.card = card;
        }

        @Override
        public Card drawCard() {

            drawCalled = true;

            return card;
        }
    }

    private static class MockPresenter implements PlayerHitOutputBoundary {
        PlayerHitOutputData outputData;
        boolean presentCalled = false;

        @Override
        public void present(PlayerHitOutputData outputData) {
            this.presentCalled = true;
            this.outputData = outputData;
        }
    }

    private BlackjackPlayer initiatePlayerWithOneHand() {

        BlackjackPlayer player = new BlackjackPlayer("TestPlayer_1");
        Hand hand = new Hand("testHand");

        player.addHand(hand);

        return player;
    }

    private BlackjackPlayer initiatePlayerWithTwoHands() {

        BlackjackPlayer player = new BlackjackPlayer("TestPlayer_2");
        Hand firstHand = new Hand("TestHand_1");
        Hand secondHand = new Hand("TestHand_2");

        player.addHand(firstHand);
        player.addHand(secondHand);

        return player;
    }

    private BlackjackGame createGame(BlackjackPlayer player) {

        BlackjackDealer dealer = new BlackjackDealer();

        return new BlackjackGame("TestDeck", dealer, player);
    }

    @Test
    void testAddNumberCardToPlayerWithOneHand() {

        BlackjackPlayer player = initiatePlayerWithOneHand();
        Hand hand = player.getHands().get(0);
        Card card = new Card("0H", "TestUrl", "10", "HEARTS");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);
        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);
        PlayerHitInputData inputData = new PlayerHitInputData(player, false);

        assertFalse(hand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(hand, presenter.outputData.getHandAfterHit());
        assertTrue(hand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
        assertFalse(presenter.outputData.isSplitHand());
    }

    @Test
    void testAddFaceCardToPlayerWithOneHand() {

        BlackjackPlayer player = initiatePlayerWithOneHand();
        Hand hand = player.getHands().get(0);
        Card card = new Card("QD", "TestUrl", "QUEEN", "DIAMONDS");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);
        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);
        PlayerHitInputData inputData = new PlayerHitInputData(player, false);

        assertFalse(hand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(hand, presenter.outputData.getHandAfterHit());
        assertTrue(hand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
        assertFalse(presenter.outputData.isSplitHand());
    }

    @Test
    void testAddAceCardToPlayerWithOneHand() {

        BlackjackPlayer player = initiatePlayerWithOneHand();
        Hand hand = player.getHands().get(0);

        Card card = new Card("AH", "TestUrl", "ACE", "HEARTS");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player, false);

        assertFalse(hand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(hand, presenter.outputData.getHandAfterHit());
        assertTrue(hand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
        assertFalse(presenter.outputData.isSplitHand());
    }

    @Test
    void testAddNumberCardToPlayerWithTwoHands_FirstHand() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        Card card = new Card("6H", "TestUrl", "6", "HEARTS");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player,false);

        assertFalse(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(firstHand, presenter.outputData.getHandAfterHit());
        assertTrue(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
    }

    @Test
    void testAddFaceCardToPlayerWithTwoHands_FirstHand() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        Card card = new Card("KS", "TestUrl", "KING", "SPADES");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player,false);

        assertFalse(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(firstHand, presenter.outputData.getHandAfterHit());
        assertTrue(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
    }

    @Test
    void testAddAceCardToPlayerWithTwoHands_FirstHand() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        Card card = new Card("AS", "TestUrl", "ACE", "SPADES");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player,false);

        assertFalse(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(firstHand, presenter.outputData.getHandAfterHit());
        assertTrue(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
    }

    @Test
    void testAddNumberCardToPlayerWithTwoHands_SplitHand() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        Card card = new Card("3H", "TestUrl", "3", "HEARTS");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player,true);

        assertFalse(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(secondHand, presenter.outputData.getHandAfterHit());
        assertTrue(secondHand.getCards().contains(card));
        assertFalse(firstHand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
        assertTrue(presenter.outputData.isSplitHand());
    }

    @Test
    void testAddFaceCardToPlayerWithTwoHands_SplitHand() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        Card card = new Card("JS", "TestUrl", "JACK", "SPADES");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player,true);

        assertFalse(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(secondHand, presenter.outputData.getHandAfterHit());
        assertTrue(secondHand.getCards().contains(card));
        assertFalse(firstHand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
        assertTrue(presenter.outputData.isSplitHand());
    }

    @Test
    void testAddAceCardToPlayerWithTwoHands_SplitHand() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        Card card = new Card("AC", "TestUrl", "ACE", "CLUBS");
        MockDeck deck = new MockDeck(card);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player,true);

        assertFalse(firstHand.getCards().contains(card));
        assertFalse(secondHand.getCards().contains(card));
        assertEquals("InGame", game.getResult());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(secondHand, presenter.outputData.getHandAfterHit());
        assertTrue(secondHand.getCards().contains(card));
        assertFalse(firstHand.getCards().contains(card));
        assertFalse(presenter.outputData.isBust());
        assertEquals("InGame", game.getResult());
        assertTrue(presenter.outputData.isSplitHand());
    }

    @Test
    void testPlayerBustUpdatesGameAndPresenter() {

        BlackjackPlayer player = initiatePlayerWithOneHand();
        Hand hand = player.getHands().get(0);

        Card cardKingDiamonds = new Card("KD", "TestUrl", "KING", "DIAMONDS");
        Card cardQueenHearts = new Card("QH", "TestUrl", "QUEEN", "HEARTS");
        Card cardTwoClubs = new Card("2H", "TestUrl", "2", "CLUBS");

        MockDeck deck = new MockDeck(cardTwoClubs);
        MockPresenter presenter = new MockPresenter();
        BlackjackGame game = createGame(player);

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);

        PlayerHitInputData inputData = new PlayerHitInputData(player, false);

        assertEquals("InGame", game.getResult());

        hand.addCard(cardKingDiamonds);
        hand.addCard(cardQueenHearts);

        assertEquals(20, hand.getHandTotalNumber());
        assertFalse(hand.isBust());

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(hand, presenter.outputData.getHandAfterHit());
        assertTrue(hand.getCards().contains(cardTwoClubs));
        assertTrue(hand.isBust());
        assertTrue(presenter.outputData.isBust());
        assertEquals("PlayerLose", game.getResult());
        assertFalse(presenter.outputData.isSplitHand());
    }

    @Test
    void testFirstHandBustInSplitGame_DoesNotSetPlayerLose() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        BlackjackGame game = createGame(player);
        game.setSplitted(true);

        Card cardKingDiamonds = new Card("KD", "TestUrl", "KING", "DIAMONDS");
        Card cardQueenHearts = new Card("QH", "TestUrl", "QUEEN", "HEARTS");
        firstHand.addCard(cardKingDiamonds);
        firstHand.addCard(cardQueenHearts);

        assertEquals(20, firstHand.getHandTotalNumber());
        assertFalse(firstHand.isBust());
        assertEquals("InGame", game.getResult());

        Card cardTwoClubs = new Card("2C", "TestUrl", "2", "CLUBS");
        MockDeck deck = new MockDeck(cardTwoClubs);
        MockPresenter presenter = new MockPresenter();

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);
        PlayerHitInputData inputData = new PlayerHitInputData(player, false);

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(firstHand, presenter.outputData.getHandAfterHit());

        assertTrue(firstHand.isBust());
        assertTrue(presenter.outputData.isBust());
        assertFalse(presenter.outputData.isSplitHand());

        assertEquals("InGame", game.getResult());
        assertEquals("", game.getSecondResult());
    }

    @Test
    void testSplitHandBustInSplitGame_SetsPlayerLose() {

        BlackjackPlayer player = initiatePlayerWithTwoHands();
        Hand firstHand = player.getHands().get(0);
        Hand secondHand = player.getHands().get(1);

        BlackjackGame game = createGame(player);
        game.setSplitted(true);

        Card cardKingDiamonds = new Card("KD", "TestUrl", "KING", "DIAMONDS");
        Card cardQueenHearts = new Card("QH", "TestUrl", "QUEEN", "HEARTS");
        secondHand.addCard(cardKingDiamonds);
        secondHand.addCard(cardQueenHearts);

        assertEquals(20, secondHand.getHandTotalNumber());
        assertFalse(secondHand.isBust());
        assertEquals("InGame", game.getResult());

        Card cardTwoClubs = new Card("2C", "TestUrl", "2", "CLUBS");
        MockDeck deck = new MockDeck(cardTwoClubs);
        MockPresenter presenter = new MockPresenter();

        PlayerHitInteractor interactor = new PlayerHitInteractor(deck, presenter, game);
        PlayerHitInputData inputData = new PlayerHitInputData(player, true);

        interactor.execute(inputData);

        assertTrue(deck.drawCalled);
        assertTrue(presenter.presentCalled);
        assertSame(secondHand, presenter.outputData.getHandAfterHit());

        assertTrue(secondHand.isBust());
        assertTrue(presenter.outputData.isBust());
        assertTrue(presenter.outputData.isSplitHand());

        assertEquals("InGame", game.getResult());
        assertEquals("PlayerLose", game.getSecondResult());
    }
}