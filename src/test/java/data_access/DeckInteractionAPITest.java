package data_access;

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

}
