package entity;

/**
 * I changed the type of value from Integer to String because according to the format, we will get something like "JACK"
 * "ACE", and "8". I created a new attribute "number" to represent the numeric value of a card.
 * Since the value of Ace can switch between 1 and 11, I default it to 11.
 */
public class Card {
    private final String code;
    private final String imageUrl;
    private final String value;
    private final String suit;
    private Integer numValue;

    public Card(String code, String imageUrl, String value, String suit) {
        this.code = code;
        this.imageUrl = imageUrl;
        this.value = value;
        this.suit = suit;
        if (value.equals("JACK") || value.equals("QUEEN") || value.equals("KING")) { this.numValue = 10; }
        else if (value.equals("ACE")) { this.numValue = 11; }
        else { this.numValue = Integer.parseInt(value); }
    }

    public String getCode() { return code; }
    public String getImageUrl() { return imageUrl; }
    public String getValue() { return value; }
    public String getSuit() { return suit; }
    public boolean isAce() { return code.charAt(0) == 'A'; }
    public void setNumValue(Integer numValue) { this.numValue = numValue; }
    public Integer getNumValue() { return numValue;}
}
