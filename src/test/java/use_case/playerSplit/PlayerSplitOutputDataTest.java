package use_case.playerSplit;

import entity.BlackjackDealer;
import entity.BlackjackGame;
import entity.BlackjackPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class PlayerSplitOutputDataTest {

    @Test
    void testGetGameReturnsProvidedInstance() {
        BlackjackPlayer player = new BlackjackPlayer("testPlayer");
        BlackjackGame game = new BlackjackGame("deck-123", new BlackjackDealer(), player);

        PlayerSplitOutputData outputData = new PlayerSplitOutputData(game);

        assertSame(game, outputData.getGame());
    }
}
