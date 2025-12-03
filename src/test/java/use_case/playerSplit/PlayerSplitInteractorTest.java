package use_case.playerSplit;

import entity.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class PlayerSplitInteractorTest {

    private static class DeckStub implements PlayerSplitDataAccessInterface {
        private final Queue<Card> cards;
        private boolean throwError;
        private int drawCalls;
        private String lastDeckId;

        DeckStub(Card... cards) {
            this.cards = new ArrayDeque<>(Arrays.asList(cards));
        }

        void setThrowError(boolean throwError) {
            this.throwError = throwError;
        }

        int getDrawCalls() {
            return drawCalls;
        }

        String getLastDeckId() {
            return lastDeckId;
        }

        @Override
        public Card drawCard(String deckId) throws IOException {
            drawCalls++;
            lastDeckId = deckId;
            if (throwError) {
                throw new IOException("Deck unavailable");
            }
            return cards.poll();
        }
    }

    private static class PresenterStub implements PlayerSplitOutputBoundary {
        private PlayerSplitOutputData outputData;
        private String errorMessage;
        private boolean successCalled;
        private boolean failCalled;

        @Override
        public void present(PlayerSplitOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void presentFailView(String message) {
            this.failCalled = true;
            this.errorMessage = message;
        }
    }

    private BlackjackGame createGameWithPlayer(String username) {
        BlackjackPlayer player = new BlackjackPlayer(username);
        return new BlackjackGame("deck-123", new BlackjackDealer(), player);
    }

    private Hand createSplittableHand() {
        Hand hand = new Hand("primary");
        hand.addCard(new Card("KH", "image", "KING", "HEARTS"));
        hand.addCard(new Card("KC", "image", "KING", "CLUBS"));
        return hand;
    }

    @Test
    void alreadySplitShowsError() {
        BlackjackGame game = createGameWithPlayer("player1");
        game.setSplitted(true);
        DeckStub deckStub = new DeckStub();
        PresenterStub presenter = new PresenterStub();
        PlayerSplitInteractor interactor = new PlayerSplitInteractor(game, deckStub, presenter);

        interactor.execute(new PlayerSplitInputData(game));

        assertTrue(presenter.failCalled);
        assertEquals("You have already split this hand.", presenter.errorMessage);
        assertFalse(presenter.successCalled);
        assertEquals(0, deckStub.getDrawCalls());
    }

    @Test
    void noHandAvailableShowsError() {
        BlackjackGame game = createGameWithPlayer("player1");
        DeckStub deckStub = new DeckStub();
        PresenterStub presenter = new PresenterStub();
        PlayerSplitInteractor interactor = new PlayerSplitInteractor(game, deckStub, presenter);

        interactor.execute(new PlayerSplitInputData(game));

        assertTrue(presenter.failCalled);
        assertEquals("No hand available to split.", presenter.errorMessage);
        assertFalse(presenter.successCalled);
        assertEquals(0, deckStub.getDrawCalls());
    }

    @Test
    void nonSplittableHandShowsError() {
        BlackjackGame game = createGameWithPlayer("player1");
        Hand hand = new Hand("primary");
        hand.addCard(new Card("5H", "image", "5", "HEARTS"));
        hand.addCard(new Card("9D", "image", "9", "DIAMONDS"));
        game.getPlayer().addHand(hand);
        DeckStub deckStub = new DeckStub();
        PresenterStub presenter = new PresenterStub();
        PlayerSplitInteractor interactor = new PlayerSplitInteractor(game, deckStub, presenter);

        interactor.execute(new PlayerSplitInputData(game));

        assertTrue(presenter.failCalled);
        assertEquals("Current hand cannot be split.", presenter.errorMessage);
        assertFalse(presenter.successCalled);
        assertEquals(0, deckStub.getDrawCalls());
        assertFalse(game.isSplitted());
    }

    @Test
    void deckErrorShowsFailView() {
        BlackjackGame game = createGameWithPlayer("player1");
        Hand hand = createSplittableHand();
        game.getPlayer().addHand(hand);
        DeckStub deckStub = new DeckStub(new Card("7S", "image", "7", "SPADES"));
        deckStub.setThrowError(true);
        PresenterStub presenter = new PresenterStub();
        PlayerSplitInteractor interactor = new PlayerSplitInteractor(game, deckStub, presenter);

        interactor.execute(new PlayerSplitInputData(game));

        assertTrue(presenter.failCalled);
        assertEquals("Problem drawing cards for split.", presenter.errorMessage);
        assertFalse(presenter.successCalled);
        assertTrue(deckStub.getDrawCalls() >= 1);
        assertFalse(game.isSplitted());
        assertEquals(1, game.getPlayer().getHandsCount());
    }

    @Test
    void successfulSplitAddsHandAndUpdatesGame() {
        BlackjackGame game = createGameWithPlayer("player1");
        Hand hand = createSplittableHand();
        game.getPlayer().addHand(hand);
        Card firstDraw = new Card("5D", "image", "5", "DIAMONDS");
        Card secondDraw = new Card("9S", "image", "9", "SPADES");
        DeckStub deckStub = new DeckStub(firstDraw, secondDraw);
        PresenterStub presenter = new PresenterStub();
        PlayerSplitInteractor interactor = new PlayerSplitInteractor(game, deckStub, presenter);

        interactor.execute(new PlayerSplitInputData(game));

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);
        assertSame(game, presenter.outputData.getGame());
        assertEquals(2, deckStub.getDrawCalls());
        assertEquals("deck-123", deckStub.getLastDeckId());
        assertTrue(game.isSplitted());
        assertEquals(2, game.getPlayer().getHandsCount());

        Hand primaryHand = game.getPlayer().getHands().get(0);
        Hand splitHand = game.getPlayer().getHands().get(1);
        assertEquals(2, primaryHand.getCards().size());
        assertEquals(2, splitHand.getCards().size());
        assertTrue(primaryHand.getCards().contains(firstDraw));
        assertTrue(splitHand.getCards().contains(secondDraw));
    }
}
