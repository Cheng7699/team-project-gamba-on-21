package entity;

/**
 * I changed the type of value from Integer to String because according to the format, we will get something like "JACK"
 * "ACE", and "8". I created a new attribute "number" to represent the numeric value of a card.
 * Since the value of Ace can switch between 1 and 11, I default it to 11.
 */
public class Card {
    private final String code;
    private final String imageUrl;
    private String value;
    private Integer number;
    private final String suit;

    public Card(String code, String imageUrl, String value, Integer number, String suit) {
        this.code = code;
        this.imageUrl = imageUrl;
        this.value = value;
        this.number = number;
        this.suit = suit;
    }

    public String getCode() { return code; }
    public String getImageUrl() { return imageUrl; }
    public String getValue() { return value; }
    public Integer getNumber() { return number; }
    public String getSuit() { return suit; }
    public boolean isAce() { return value.equals("ACE"); }

    public void setNumber(Integer number) { this.number = number; }
}
