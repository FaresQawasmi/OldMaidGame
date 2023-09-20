package Entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Deck {
    private List<Card> cards;
    public Deck() {
        cards = new LinkedList<>();
        initializeDeck();
    }
    private void initializeDeck() {
        String[] suits = {"♠", "♥", "♣", "♦"};
        String[] values = {"2", "3", "4", "5",
                "6", "7", "8", "9", "10",
                "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String value : values) {
                Card card = new Card(value, suit, false);
                cards.add(card);
            }
        }
        cards.add(new Card("Joker", "", true));
    }
    public void shuffle() {
        Collections.shuffle(cards);
    }
    public List<Card> deal(int numCards) {
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < numCards && !cards.isEmpty(); i++) {
            dealtCards.add(cards.remove(0));
        }
        return dealtCards;
    }
}



