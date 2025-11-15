package entity;

import java.util.ArrayList;
import java.util.List;

public class BlackjackPlayer {
    private final String username;
    private final List<Hand> hands = new ArrayList<>();

    public BlackjackPlayer(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
    public List<Hand> getHands() { return this.hands; }
    public Integer getHandsCount() { return this.hands.size(); }

    public void addHand(Hand hand) { this.hands.add(hand); }
}