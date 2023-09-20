package Entities;

public class Card {
    private String value;
    private String suit;
    private boolean isJoker;
    public Card(String value, String suit, boolean isJoker) {
        this.value = value;
        this.suit = suit;
        this.isJoker = isJoker;
    }
    public String getValue() {
        return value;
    }
    public String getSuit() {
        return suit;
    }
    public boolean isJoker() {
        return isJoker;
    }
    @Override
    public String toString() {
        if (isJoker) {
            return "Joker";
        }
        return value + " of " + getSymbolForSuit(suit);
    }
    private String getSymbolForSuit(String suit) {
        switch (suit) {
            case "Spades":
                return "♠";
            case "Hearts":
                return "♥";
            case "Clubs":
                return "♣";
            case "Diamonds":
                return "♦";
            default:
                return suit;
        }
    }
}
