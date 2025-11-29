package entity;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HandTest {

    @Test
    public void testNewHandIsEmptyAndZeroTotalNumber() {

        Hand hand = new Hand("TestHand");

        assertEquals("TestHand", hand.getHandID());
        assertTrue(hand.getCards().isEmpty());
        assertEquals(0, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
        assertFalse(hand.splittable());
    }

    @Test
    public void testAddOneNumberCard() {

        Hand hand = new Hand("TestHand");
        Card card = new Card("8H", "TestUrl", "8", "HEARTS");

        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertEquals(8, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
    }

    @Test
    public void testAddOneAceCard() {

        Hand hand = new Hand("TestHand");
        Card card = new Card("AC", "TestUrl", "ACE", "CLUBS");

        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertEquals(11, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
    }

    @Test
    public void testAddOneJackCard() {

        Hand hand = new Hand("TestHand");
        Card card = new Card("JS", "TestUrl", "JACK", "SPADES");

        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertEquals(10, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
    }

    @Test
    public void testAddOneQueenCard() {

        Hand hand = new Hand("TestHand");
        Card card = new Card("QS", "TestUrl", "QUEEN", "SPADES");

        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertEquals(10, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
    }

    @Test
    public void testAddOneKingCard() {

        Hand hand = new Hand("TestHand");
        Card card = new Card("KD", "TestUrl", "KING", "DIAMONDS");

        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertEquals(10, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
    }

    @Test
    public void testAddMultipleCardsWithoutBust() {

        Hand hand = new Hand("TestHand");
        Card[] cards = new Card[]{
                new Card("KD", "TestUrl", "KING", "DIAMONDS"),
                new Card("2C", "TestUrl", "2", "CLUBS"),
        };

        hand.addCards(cards);

        assertEquals(2, hand.getCards().size());
        assertEquals(12, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
    }

    @Test
    public void testAddMultipleCardsWithBust() {

        Hand hand = new Hand("TestHand");
        Card[] cards = new Card[]{
                new Card("KD", "TestUrl", "KING", "DIAMONDS"),
                new Card("QC", "TestUrl", "QUEEN", "CLUBS"),
                new Card("3H", "TestUrl", "3", "HEARTS"),
        };

        hand.addCards(cards);

        assertEquals(3, hand.getCards().size());
        assertEquals(23, hand.getHandTotalNumber());
        assertTrue(hand.isBust());
    }


    @Test
    public void testOneAceChangesFromElevenToOneToAvoidBust() {

        Hand hand = new Hand("TestHand");

        Card cardAceSpades = new Card("AS", "TestUrl", "ACE", "SPADES");
        Card cardNineHearts = new Card("9H", "TestUrl", "9", "HEARTS");
        Card cardEightClubs = new Card("8C", "TestUrl", "8", "CLUBS");

        hand.addCard(cardAceSpades);

        assertEquals(11, hand.getHandTotalNumber());

        hand.addCard(cardNineHearts);

        assertEquals(20, hand.getHandTotalNumber());

        hand.addCard(cardEightClubs);

        assertEquals(18, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
    }

    @Test
    public void testMultipleAcesChangeFromElevenToOneToAvoidBust() {

        Hand hand = new Hand("TestHand");

        Card cardAceSpades = new Card("AS", "TestUrl", "ACE", "SPADES");
        Card cardAceClubs = new Card("AC", "TestUrl", "ACE", "CLUBS");
        Card cardNineHearts = new Card("9H", "TestUrl", "9", "HEARTS");
        Card cardEightClubs = new Card("8C", "TestUrl", "8", "CLUBS");
        Card cardSevenDiamonds = new Card("7D", "TestUrl", "7", "DIAMONDS");

        hand.addCard(cardAceSpades);

        assertEquals(11, hand.getHandTotalNumber());

        hand.addCard(cardAceClubs);

        assertEquals(12, hand.getHandTotalNumber());

        hand.addCard(cardNineHearts);

        assertEquals(21, hand.getHandTotalNumber());
        assertFalse(hand.isBust());

        hand.addCard(cardEightClubs);

        assertEquals(19, hand.getHandTotalNumber());
        assertFalse(hand.isBust());

        hand.addCard(cardSevenDiamonds);

        assertEquals(26, hand.getHandTotalNumber());
        assertTrue(hand.isBust());
    }

    @Test
    public void testRemoveCard() {

        Hand hand = new Hand("TestHand");
        Card firstCard = new Card("9S", "TestUrl", "9", "SPADES");
        Card secondCard = new Card("7S", "TestUrl", "7", "SPADES");

        hand.addCard(firstCard);
        hand.addCard(secondCard);

        Card cardRemoved = hand.removeCard(0);

        assertEquals(firstCard, cardRemoved);
        assertEquals(1, hand.getCards().size());
        assertEquals(7, hand.getHandTotalNumber());
    }

    @Test
    public void testSplittableIsTrueForTwoCardsWithSameNumericValue() {

        Hand hand = new Hand("TestHand");

        Card cardJackHearts = new Card("JH", "TestUrl", "JACK", "HEARTS");
        Card cardJackSpades = new Card("JS", "TestUrl","JACK", "SPADES");

        hand.addCard(cardJackHearts);
        hand.addCard(cardJackSpades);

        assertTrue(hand.splittable());
    }

    @Test
    public void testSplittableIsTrueForTwoAces() {

        Hand hand = new Hand("TestHand");

        Card cardAceHearts = new Card("AH", "TestUrl", "ACE", "HEARTS");
        Card cardAceSpades = new Card("AS", "TestUrl","ACE", "SPADES");

        hand.addCard(cardAceHearts);
        hand.addCard(cardAceSpades);

        assertTrue(hand.splittable());
    }

    @Test
    public void testSplittableIsFalseForSizeNotTwo() {

        Hand hand = new Hand("TestHand");

        Card cardJackHearts = new Card("JH", "TestUrl", "JACK", "HEARTS");
        Card cardJackSpades = new Card("JS", "TestUrl","JACK", "SPADES");
        Card cardSevenSpades = new Card("7S", "TestUrl", "7", "SPADES");

        hand.addCard(cardJackHearts);

        assertFalse(hand.splittable());

        hand.addCard(cardJackSpades);

        assertTrue(hand.splittable());

        hand.addCard(cardSevenSpades);

        assertFalse(hand.splittable());
    }

    @Test
    public void testSplittableIsFalseForTwoDifferenetCards() {

        Hand hand = new Hand("TestHand");

        Card cardJackHearts = new Card("JH", "TestUrl", "JACK", "HEARTS");
        Card cardSevenSpades = new Card("7S", "TestUrl", "7", "SPADES");

        hand.addCard(cardJackHearts);

        assertFalse(hand.splittable());

        hand.addCard(cardSevenSpades);

        assertFalse(hand.splittable());
    }

    @Test
    public void testReset() {

        Hand hand = new Hand("TestHand");

        Card cardJackHearts = new Card("JH", "TestUrl", "JACK", "HEARTS");
        Card cardSevenSpades = new Card("7S", "TestUrl", "7", "SPADES");
        Card cardAceSpades = new Card("AS", "TestUrl","ACE", "SPADES");
        Card cardAceHearts = new Card("AH", "TestUrl", "ACE", "HEARTS");
        Card cardEightClubs = new Card("8C", "TestUrl", "8", "CLUBS");

        hand.addCard(cardJackHearts);
        hand.addCard(cardSevenSpades);
        hand.addCard(cardAceSpades);

        assertFalse(hand.getCards().isEmpty());

        assertEquals(18, hand.getHandTotalNumber());

        hand.addCard(cardAceHearts);

        assertFalse(hand.getCards().isEmpty());
        assertEquals(19, hand.getHandTotalNumber());

        hand.addCard(cardEightClubs);

        assertFalse(hand.getCards().isEmpty());
        assertEquals(27, hand.getHandTotalNumber());
        assertTrue(hand.isBust());
        assertFalse(hand.splittable());

        hand.reset();

        assertTrue(hand.getCards().isEmpty());
        assertEquals(0, hand.getHandTotalNumber());
        assertFalse(hand.isBust());
        assertFalse(hand.splittable());
    }
}
