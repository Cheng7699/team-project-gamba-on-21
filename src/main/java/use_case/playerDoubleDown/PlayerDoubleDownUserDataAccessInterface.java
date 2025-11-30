package use_case.playerDoubleDown;

import entity.Accounts;
import entity.Card;

public interface PlayerDoubleDownUserDataAccessInterface {
    Accounts get(String username);
    void save(Accounts account);
    String getCurrentUsername();
    Card drawCard();
}

