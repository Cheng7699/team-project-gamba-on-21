package data_access;

import entity.Card;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class DeckInteractionAPITest {
    @Test
    void testDeckInteractionAPI() throws IOException {
        DeckInteractionAPI apiInteractor = new DeckInteractionAPI();
        String deck1 = apiInteractor.createDeck(1, true, true);
        String deck2 = apiInteractor.createDeck(1, false, false);
        System.out.print(deck1+","+deck2);
    }

    @Test
    void testError() throws IOException {
        DeckInteractionAPI apiInteractor = new DeckInteractionAPI();
        Card card = apiInteractor.drawCard("a28jd973");
    }

    @Test
    void testDrawEmptyDeck() throws IOException {
        DeckInteractionAPI apiInteractor = new DeckInteractionAPI();
        String deckId = apiInteractor.createDeck(1, true, true);
        for (int i=0; i<56;i++) {
            Card card = apiInteractor.drawCard(deckId);
            System.out.print(card.getCode());
        }
    }

    @Test
    void testDrawCard() throws IOException {
        DeckInteractionAPI apiInteractor = new DeckInteractionAPI();
        Card card = apiInteractor.drawCard("rp7zhzmc2nbf");
        System.out.print(card.getCode());
    }
}
