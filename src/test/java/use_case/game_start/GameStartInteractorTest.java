package use_case.game_start;

import entity.BlackjackDealer;
import entity.BlackjackGame;
import entity.BlackjackPlayer;
import entity.Card;
import entity.Hand;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GameStartInteractor}.
 *
 * These tests are written to exercise every executable line in GameStartInteractor
 * (success paths + all IOException branches).
 */
public class GameStartInteractorTest {

    /**
     * Simple configurable mock for the data access interface.
     */
    private static class MockGameStartDataAccess implements GameStartDataAccessInterface {

        String createdDeckIdToReturn = "NEW_DECK_ID";

        Card[] dealerCardsToReturn;
        Card[] playerCardsToReturn;

        boolean shuffleThrows;
        boolean createDeckThrows;
        boolean drawThrows;
        boolean addCardsThrows;

        boolean shuffleCalled;
        boolean createDeckCalled;
        int drawCardsCallCount;
        int addCardsCallCount;

        String lastShuffledDeckId;
        String lastCreateDeckDeckIdParam;
        String lastAddCardsPileName;

        @Override
        public String createDeck(Boolean shuffled, Boolean jokers) throws IOException {
            createDeckCalled = true;
            if (createDeckThrows) {
                throw new IOException("createDeck error");
            }
            // record that method was called, but the exact parameters do not matter
            lastCreateDeckDeckIdParam = createdDeckIdToReturn;
            return createdDeckIdToReturn;
        }

        @Override
        public Card drawCard(String deckId) throws IOException {
            // Not used in GameStartInteractor; implement just to satisfy the interface.
            return null;
        }

        @Override
        public Card[] drawCards(String deckId, Integer number) throws IOException {
            drawCardsCallCount++;
            if (drawThrows) {
                throw new IOException("drawCards error");
            }

            // First call is for dealer, second for player.
            if (drawCardsCallCount == 1 && dealerCardsToReturn != null) {
                return dealerCardsToReturn;
            }
            if (drawCardsCallCount == 2 && playerCardsToReturn != null) {
                return playerCardsToReturn;
            }
            return new Card[0];
        }

        @Override
        public void addCard(String deckId, String pileName, Card card) throws IOException {
            // Not used in GameStartInteractor.
        }

        @Override
        public void addCards(String deckId, String pileName, Card[] cards) throws IOException {
            addCardsCallCount++;
            lastAddCardsPileName = pileName;
            if (addCardsThrows) {
                throw new IOException("addCards error");
            }
        }

        @Override
        public void shuffle(String deckId) throws IOException {
            shuffleCalled = true;
            lastShuffledDeckId = deckId;
            if (shuffleThrows) {
                throw new IOException("shuffle error");
            }
        }
    }

    /**
     * Simple presenter mock that records calls.
     */
    private static class MockGameStartPresenter implements GameStartOutputBoundary {

        int presentCallCount = 0;
        GameStartOutputData lastOutputData;
        final List<GameStartOutputData> allOutputData = new ArrayList<>();

        boolean failViewCalled = false;
        final List<String> failMessages = new ArrayList<>();

        @Override
        public void present(GameStartOutputData outputData) {
            presentCallCount++;
            lastOutputData = outputData;
            allOutputData.add(outputData);
        }

        @Override
        public void presentFailView(String message) {
            failViewCalled = true;
            failMessages.add(message);
        }
    }

    private Card[] createCards(String prefix) {
        return new Card[]{
                new Card(prefix + "1", "image-url-1", "ACE", "SPADES"),
                new Card(prefix + "2", "image-url-2", "9", "HEARTS")
        };
    }

    private BlackjackGame createGameWithDeck(String deckId) {
        BlackjackDealer dealer = new BlackjackDealer();
        BlackjackPlayer player = new BlackjackPlayer("TestPlayer");

        BlackjackGame game = new BlackjackGame(deckId, dealer, player);

        // Give the player some stale hands and mark the game as split and finished
        player.addHand(new Hand("oldHand1"));
        player.addHand(new Hand("oldHand2"));
        game.setSplitted(true);
        game.playerWin(); // result becomes PlayerWin and state GameOver

        return game;
    }

    @Test
    public void execute_existingDeck_nonZeroBet_successfulFlow() {
        MockGameStartDataAccess api = new MockGameStartDataAccess();
        MockGameStartPresenter presenter = new MockGameStartPresenter();

        // Deck already exists: branch where shuffle is called.
        BlackjackGame game = createGameWithDeck("EXISTING_DECK");

        api.dealerCardsToReturn = createCards("D");
        api.playerCardsToReturn = createCards("P");

        GameStartInteractor interactor = new GameStartInteractor(api, presenter);
        GameStartInputData inputData = new GameStartInputData(game, 50);

        interactor.execute(inputData);

        // Game state should be reset at start of round
        assertEquals("InGame", game.getResult());
        assertEquals("PlayerTurn", game.getState());

        // Shuffle should have been called, but no createDeck.
        assertTrue(api.shuffleCalled);
        assertEquals("EXISTING_DECK", api.lastShuffledDeckId);
        assertFalse(api.createDeckCalled);

        // Old hands should have been cleared and replaced with a single new hand.
        assertEquals(1, game.getPlayer().getHandsCount());
        Hand playerHand = game.getPlayer().getHands().get(0);
        assertEquals("playerHand1", playerHand.getHandID());
        assertEquals(2, playerHand.getCards().size());

        // Dealer should have a new hand with two cards.
        assertNotNull(game.getDealer().getHand());
        assertEquals("dealerHand", game.getDealer().getHand().getHandID());
        assertEquals(2, game.getDealer().getHand().getCards().size());

        // Split flag should have been reset.
        assertFalse(game.isSplitted());

        // Presenter should have been called once with the final output data.
        assertEquals(1, presenter.presentCallCount);
        assertSame(game, presenter.lastOutputData.getGame());
        assertEquals(50, presenter.lastOutputData.getBetAmount());

        // No fail view expected in this successful flow.
        assertFalse(presenter.failViewCalled);
    }

    @Test
    public void execute_existingDeck_zeroBet_callsPresenterTwice() {
        MockGameStartDataAccess api = new MockGameStartDataAccess();
        MockGameStartPresenter presenter = new MockGameStartPresenter();

        BlackjackGame game = createGameWithDeck("EXISTING_DECK");

        api.dealerCardsToReturn = createCards("D");
        api.playerCardsToReturn = createCards("P");

        GameStartInteractor interactor = new GameStartInteractor(api, presenter);
        GameStartInputData inputData = new GameStartInputData(game, 0);

        interactor.execute(inputData);

        // Because bet is 0, the early "do nothing" present is triggered,
        // and the normal final present at the end also runs.
        assertEquals(2, presenter.presentCallCount);
        assertEquals(0, presenter.allOutputData.get(0).getBetAmount());
        assertEquals(0, presenter.allOutputData.get(1).getBetAmount());
    }

    @Test
    public void execute_existingDeck_shuffleIOException_triggersFailViewButContinues() {
        MockGameStartDataAccess api = new MockGameStartDataAccess();
        api.shuffleThrows = true;
        api.dealerCardsToReturn = createCards("D");
        api.playerCardsToReturn = createCards("P");

        MockGameStartPresenter presenter = new MockGameStartPresenter();

        BlackjackGame game = createGameWithDeck("EXISTING_DECK");

        GameStartInteractor interactor = new GameStartInteractor(api, presenter);
        GameStartInputData inputData = new GameStartInputData(game, 25);

        interactor.execute(inputData);

        // We should have attempted to shuffle and then reported the shuffle error.
        assertTrue(api.shuffleCalled);
        assertTrue(presenter.failViewCalled);
        assertTrue(presenter.failMessages.contains("Error while shuffling deck"));

        // Despite shuffle error, the rest of the flow still runs and we get final output.
        assertEquals(1, presenter.presentCallCount);
        assertSame(game, presenter.lastOutputData.getGame());
    }

    @Test
    public void execute_newDeck_createDeckIOException_setsErrorDeckId() {
        MockGameStartDataAccess api = new MockGameStartDataAccess();
        api.createDeckThrows = true;
        api.dealerCardsToReturn = createCards("D");
        api.playerCardsToReturn = createCards("P");

        MockGameStartPresenter presenter = new MockGameStartPresenter();

        // Empty deck ID forces the "createDeck" branch.
        BlackjackGame game = createGameWithDeck("");
        GameStartInteractor interactor = new GameStartInteractor(api, presenter);
        GameStartInputData inputData = new GameStartInputData(game, 10);

        interactor.execute(inputData);

        // When createDeck throws, deck id is set to "error in deck creation".
        assertEquals("error in deck creation", game.getDeckID());

        // We still proceed to draw, add cards, and present the final output.
        assertEquals(1, presenter.presentCallCount);
        assertSame(game, presenter.lastOutputData.getGame());
    }

    @Test
    public void execute_drawCardsIOException_triggersFailViewAndStops() {
        MockGameStartDataAccess api = new MockGameStartDataAccess();
        api.drawThrows = true;

        MockGameStartPresenter presenter = new MockGameStartPresenter();

        BlackjackGame game = createGameWithDeck("EXISTING_DECK");
        GameStartInteractor interactor = new GameStartInteractor(api, presenter);
        GameStartInputData inputData = new GameStartInputData(game, 15);

        interactor.execute(inputData);

        // Drawing cards failed, so we should have reported that and exited early.
        assertTrue(presenter.failViewCalled);
        assertTrue(presenter.failMessages.contains("error in drawing cards"));

        // No cards are added to piles and no final present happens after the error.
        assertEquals(0, api.addCardsCallCount);
        assertEquals(0, presenter.presentCallCount);
    }

    @Test
    public void execute_addCardsIOException_triggersFailViewButStillPresents() {
        MockGameStartDataAccess api = new MockGameStartDataAccess();
        api.dealerCardsToReturn = createCards("D");
        api.playerCardsToReturn = createCards("P");
        api.addCardsThrows = true;

        MockGameStartPresenter presenter = new MockGameStartPresenter();

        BlackjackGame game = createGameWithDeck("EXISTING_DECK");
        GameStartInteractor interactor = new GameStartInteractor(api, presenter);
        GameStartInputData inputData = new GameStartInputData(game, 30);

        interactor.execute(inputData);

        // Adding cards to piles failed, but this error is reported and execution continues.
        assertTrue(presenter.failViewCalled);
        assertTrue(presenter.failMessages.contains("error in adding cards to hands"));

        // Final present should still be called so the UI can reflect the dealt cards.
        assertEquals(1, presenter.presentCallCount);
        assertSame(game, presenter.lastOutputData.getGame());
        assertEquals(30, presenter.lastOutputData.getBetAmount());
    }

    @Test
    public void execute_newDeck_successfulFlow() {
        MockGameStartDataAccess api = new MockGameStartDataAccess();
        MockGameStartPresenter presenter = new MockGameStartPresenter();

        // Force the "new deck" branch: deckID is initially empty
        BlackjackDealer dealer = new BlackjackDealer();
        entity.BlackjackPlayer player = new entity.BlackjackPlayer("TestPlayer");
        BlackjackGame game = new BlackjackGame("", dealer, player);

        // No old hands, but it would also work if there were (they get cleared)
        // Make sure createDeck succeeds and returns a known ID
        api.createDeckThrows = false;
        api.createdDeckIdToReturn = "NEW_DECK_123";

        // Cards to be drawn successfully
        api.dealerCardsToReturn = createCards("D");
        api.playerCardsToReturn = createCards("P");

        GameStartInteractor interactor = new GameStartInteractor(api, presenter);
        GameStartInputData inputData = new GameStartInputData(game, 50);

        interactor.execute(inputData);

        // We must have gone through the "new deck" branch and the try block successfully
        assertTrue(api.createDeckCalled, "createDeck should be called");
        assertEquals("NEW_DECK_123", game.getDeckID(),
                "Deck ID should be set from the successful createDeck call");

        // Since it was a new deck, shuffle() must NOT be called
        assertFalse(api.shuffleCalled, "Existing-deck shuffle should not be called for a new deck");

        // Hands cleared + new hands created
        assertEquals(1, game.getPlayer().getHandsCount());
        assertNotNull(game.getDealer().getHand());
        assertEquals(2, game.getPlayer().getHands().get(0).getCards().size());
        assertEquals(2, game.getDealer().getHand().getCards().size());

        // No failure view, final present called once
        assertFalse(presenter.failViewCalled);
        assertEquals(1, presenter.presentCallCount);
        assertSame(game, presenter.lastOutputData.getGame());
        assertEquals(50, presenter.lastOutputData.getBetAmount());
    }


}
