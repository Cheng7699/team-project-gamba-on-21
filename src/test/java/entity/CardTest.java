package entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    public void testCreatingNumberCard() {

        Card card = new Card("5C", "TestUrl", "5", "CLUBS");

        assertEquals("5C", card.getCode());
        assertEquals("TestUrl", card.getImageUrl());
        assertEquals("5", card.getValue());
        assertEquals("CLUBS", card.getSuit());
        assertEquals(5, card.getNumValue());
    }

    @Test
    public void testCreatingAceCardWithElevenDefault() {

        Card card = new Card("AC", "TestUrl", "ACE", "CLUBS");

        assertEquals("AC", card.getCode());
        assertEquals("TestUrl", card.getImageUrl());
        assertEquals("ACE", card.getValue());
        assertEquals("CLUBS", card.getSuit());
        assertEquals(11, card.getNumValue());
    }

    @Test
    public void testCreatingJackCard() {

        Card card = new Card("JH", "TestUrl", "JACK", "HEARTS");

        assertEquals("JH", card.getCode());
        assertEquals("TestUrl", card.getImageUrl());
        assertEquals("JACK", card.getValue());
        assertEquals("HEARTS", card.getSuit());
        assertEquals(10, card.getNumValue());
    }

    @Test
    public void testCreatingQueenCard() {

        Card card = new Card("QD", "TestUrl", "QUEEN", "DIAMONDS");

        assertEquals("QD", card.getCode());
        assertEquals("TestUrl", card.getImageUrl());
        assertEquals("QUEEN", card.getValue());
        assertEquals("DIAMONDS", card.getSuit());
        assertEquals(10, card.getNumValue());
    }

    @Test
    public void testCreatingKingCard() {

        Card card = new Card("KS", "TestUrl", "KING", "SPADES");

        assertEquals("KS", card.getCode());
        assertEquals("TestUrl", card.getImageUrl());
        assertEquals("KING", card.getValue());
        assertEquals("SPADES", card.getSuit());
        assertEquals(10, card.getNumValue());
    }

    @Test
    public void testIsAce() {

        Card cardAce = new Card("AH", "TestUrl", "ACE", "HEARTS");
        Card cardNumber = new Card("7S", "TestUrl", "7", "SPADES");
        Card cardJack = new Card("JC", "TestUrl", "JACK", "CLUBS");
        Card cardQueen = new Card("QD", "TestUrl", "QUEEN", "DIAMONDS");
        Card cardKing = new Card("KS", "TestUrl", "KING", "SPADES");

        assertTrue(cardAce.isAce());
        assertFalse(cardNumber.isAce());
        assertFalse(cardJack.isAce());
        assertFalse(cardQueen.isAce());
        assertFalse(cardKing.isAce());
    }

    @Test
    public void testSetNumValue() {

        Card card = new Card("AC", "TestUrl", "ACE", "CLUBS");

        assertEquals(11, card.getNumValue());

        card.setNumValue(1);

        assertEquals(1, card.getNumValue());
        assertTrue(card.isAce());
    }

    @Test
    public void testDifferentCardsAreDistinguishable() {

        Card cardAceClubs = new Card("AC", "TestUrl", "ACE", "CLUBS");
        Card cardAceSpades = new Card("AS", "TestUrl", "ACE", "SPADES");

        Card cardNumberHearts = new Card("7H", "TestUrl", "7", "HEARTS");
        Card cardNumberDiamonds = new Card("7D", "TestUrl", "7", "DIAMONDS");

        Card cardJackClubs = new Card("JC", "TestUrl", "JACK", "CLUBS");
        Card cardJackHearts = new Card("JH", "TestUrl", "JACK", "HEARTS");

        Card cardQueenSpades = new Card("QS", "TestUrl", "QUEEN", "SPADES");
        Card cardQueenDiamonds = new Card("QD", "TestUrl", "QUEEN", "DIAMONDS");

        Card cardKingClubs = new Card("KC", "TestUrl", "KING", "CLUBS");
        Card cardKingHearts = new Card("KH", "TestUrl", "KING", "HEARTS");

        assertEquals("CLUBS", cardAceClubs.getSuit());
        assertEquals("CLUBS", cardJackClubs.getSuit());
        assertEquals("CLUBS", cardKingClubs.getSuit());

        assertEquals("SPADES", cardAceSpades.getSuit());
        assertEquals("SPADES", cardQueenSpades.getSuit());

        assertEquals("HEARTS", cardNumberHearts.getSuit());
        assertEquals("HEARTS", cardJackHearts.getSuit());
        assertEquals("HEARTS", cardKingHearts.getSuit());

        assertEquals("DIAMONDS", cardNumberDiamonds.getSuit());
        assertEquals("DIAMONDS", cardQueenDiamonds.getSuit());

        assertEquals(cardAceClubs.getNumValue(), cardAceSpades.getNumValue());
        assertEquals(cardNumberHearts.getNumValue(), cardNumberDiamonds.getNumValue());
        assertEquals(cardJackClubs.getNumValue(), cardJackHearts.getNumValue());
        assertEquals(cardQueenSpades.getNumValue(), cardQueenDiamonds.getNumValue());
        assertEquals(cardKingClubs.getNumValue(), cardKingHearts.getNumValue());

        assertNotEquals(cardAceClubs.getNumValue(), cardNumberHearts.getNumValue());
        assertNotEquals(cardQueenSpades.getNumValue(), cardNumberDiamonds.getNumValue());
        assertNotEquals(cardAceSpades.getNumValue(), cardNumberHearts.getNumValue());

        assertNotEquals(cardAceClubs.getCode(), cardNumberHearts.getCode());
        assertNotEquals(cardJackClubs.getCode(), cardKingHearts.getCode());
        assertNotEquals(cardQueenSpades.getCode(), cardNumberDiamonds.getCode());
        assertNotEquals(cardKingClubs.getCode(), cardJackHearts.getCode());
        assertNotEquals(cardAceSpades.getCode(), cardNumberHearts.getCode());

        assertNotEquals(cardAceClubs.getCode(), cardAceSpades.getCode());
        assertNotEquals(cardNumberHearts.getCode(), cardNumberDiamonds.getCode());
        assertNotEquals(cardJackClubs.getCode(), cardJackHearts.getCode());
        assertNotEquals(cardQueenSpades.getCode(), cardQueenDiamonds.getCode());
        assertNotEquals(cardKingClubs.getCode(), cardKingHearts.getCode());
    }
}
