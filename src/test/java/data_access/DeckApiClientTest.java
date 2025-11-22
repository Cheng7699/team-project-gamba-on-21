package data_access;

import entity.Card;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class DeckApiClientTest {
    @Test
    void testDeckInteractionAPI() throws IOException {
        DeckApiClient apiClient = new DeckApiClient();
        String deck1 = apiClient.createDeck(1, true, true);
        String deck2 = apiClient.createDeck(1, false, false);
        System.out.print(deck1+","+deck2);
    }

    @Test
    void testError() throws IOException {
        DeckApiClient apiClient = new DeckApiClient();
        Card card = apiClient.drawCard("a28jd973");
    }

    @Test
    void testDrawEmptyDeck() throws IOException {
        DeckApiClient apiClient = new DeckApiClient();
        String deckId = apiClient.createDeck(1, true, true);
        for (int i=0; i<56;i++) {
            Card card = apiClient.drawCard(deckId);
            System.out.print(card.getCode());
        }
    }

    @Test
    void testDrawCard() throws IOException {
        DeckApiClient apiClient = new DeckApiClient();
        Card card = apiClient.drawCard("rp7zhzmc2nbf");
        System.out.print(card.getCode());
    }

    @Test
    void testPileAddCard() throws IOException {
        DeckApiClient apiClient = new DeckApiClient();
        Card card = apiClient.drawCard("rp7zhzmc2nbf");
        apiClient.addCard("rp7zhzmc2nbf", "testPile", card);
        apiClient.listPile("rp7zhzmc2nbf", "testPile");
    }
}
