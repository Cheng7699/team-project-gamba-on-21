package use_case.playerSplit;

import entity.BlackjackDealer;
import entity.BlackjackGame;
import entity.BlackjackPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class PlayerSplitInputDataTest {

    @Test
    void testGetGameReturnsProvidedInstance() {
        BlackjackPlayer player = new BlackjackPlayer("testPlayer");
        BlackjackGame game = new BlackjackGame("deck-123", new BlackjackDealer(), player);

        PlayerSplitInputData inputData = new PlayerSplitInputData(game);

        assertSame(game, inputData.getGame());
    }
}
