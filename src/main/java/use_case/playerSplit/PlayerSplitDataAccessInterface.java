package use_case.playerSplit;

import entity.Card;

import java.io.IOException;

public interface PlayerSplitDataAccessInterface {
    Card drawCard(String deckId) throws IOException;
}
