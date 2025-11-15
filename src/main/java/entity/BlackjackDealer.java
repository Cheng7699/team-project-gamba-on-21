package entity;

import java.util.ArrayList;
import java.util.List;

public class BlackjackDealer {
    private boolean hideFirstCard;
    private Hand hand;

    public BlackjackDealer() {
        this.hideFirstCard = true;
    }

    public Hand getHand() { return hand; }
    public boolean isHideFirstCard() { return hideFirstCard; }

    public void setHand(Hand hand) { this.hand = hand; }
    public void setHideFirstCard(boolean hideFirstCard) { this.hideFirstCard = hideFirstCard; }
}
