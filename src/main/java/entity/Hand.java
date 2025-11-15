package entity;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final String handID;
    private final List<Card> cards = new ArrayList<>();
    private Integer handTotalNumber;

    public Hand(String handID) {
        this.handID = handID;
        this.handTotalNumber = 0;
    }

    public String getHandID() { return handID; }
    public List<Card> getCards() { return cards; }
    public Integer getHandTotalNumber() { return handTotalNumber; }

    /**
     * This method will automatically update the total value of all cards in the hand.
     */
    public void addCard(Card card) {
        this.cards.add(card);
        this.updateHandTotalNumber();
    }

    /**
     * This method will not check the validity of index so make sure the input is valid.
     * This method will automatically update the total value of all cards in the hand.
     */
    public Card removeCard(int index) {
        Card cardToRemove = this.cards.remove(index);
        this.updateHandTotalNumber();
        return cardToRemove;
    }

    /**
     * This method will check if the hand itself is splittable or not.
     * It will not check if the split times is greater than one or not.
     */
    public boolean splittable() {
        if (cards.size() != 2) {
            return false;
        } else if (cards.get(0).isAce() && cards.get(1).isAce()) {
            return true;
        } else {
            return cards.get(0).getNumber() == cards.get(1).getNumber();
        }
    }

    /**
     * This method will calculate the best number which the hand can get.
     * If there are Aces in the hand, we default them as 11, and calculate total number.
     * If total number is greater than 21, we set aces to 1, to make it as cloer to 21 as possible without exceeding it.
     */
    public void updateHandTotalNumber() {
        Integer result = 0;
        Integer aces = 0;
        for (Card card : this.cards) {
            result += card.getNumber();
            if (card.isAce()) { aces++; }
        }
        while (result > 21 && aces > 0) {
            result -= 10;
            aces--;
            this.updateAce();
        }
        this.handTotalNumber = result;
    }

    /**
     * This method is a helper method to reassign an Ace we changed from 11 to 1.
     */
    public void updateAce() {
        for (Card card : this.cards) {
            if (card.isAce() && card.getNumber() == 11) {
                card.setNumber(1);
            }
        }
    }

    public boolean isBust() { return this.handTotalNumber > 21; }
}
