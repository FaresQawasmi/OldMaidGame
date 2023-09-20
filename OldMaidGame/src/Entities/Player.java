package Entities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Player extends Thread {
    private String name;
    private List<Card> hand;
    private static List<Player> players;
    private static AtomicInteger currentPlayerIndex = new AtomicInteger(-1);
    private volatile boolean running = true;
    private static boolean isGameOver = false;


    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public static void initializePlayers(List<Player> playerList) {
        players = playerList;
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public int getHandSize() {
        return hand.size();
    }

    public String getPName() {
        return name;
    }

    public boolean hasJoker() {
        return hand.stream().anyMatch(Card::isJoker);
    }

    private boolean isMatchingPair(Card card1, Card card2) {
        if (card1.isJoker() || card2.isJoker()) {
            return false;
        }

        if (card1.getValue().equals(card2.getValue())) {
            String suit1 = card1.getSuit();
            String suit2 = card2.getSuit();
            return (suit1.equals("♠") && suit2.equals("♣")) ||
                    (suit1.equals("♣") && suit2.equals("♠")) ||
                    (suit1.equals("♦") && suit2.equals("♥")) ||
                    (suit1.equals("♥") && suit2.equals("♦"));
        }

        return false;
    }


    private void removeCardFromHand(Card card) {
        hand.remove(card);
    }

    public void discardPair() {
        List<Card> cardsToRemove = new ArrayList<>();
        List<Card> discardedPairs = new ArrayList<>();

        for (int i = 0; i < hand.size() - 1; i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (isMatchingPair(hand.get(i), hand.get(j))) {
                    cardsToRemove.add(hand.get(i));
                    cardsToRemove.add(hand.get(j));
                    discardedPairs.add(hand.get(i));
                    discardedPairs.add(hand.get(j));
                    break;
                }
            }
        }

        for (Card card : cardsToRemove) {
            removeCardFromHand(card);
        }

        if (!discardedPairs.isEmpty()) {
            System.out.println(name + " discarded the following pairs:");
            for (Card card : discardedPairs) {
                System.out.println(card.toString());
            }
        }
    }

    public static void stopGame() {
        isGameOver = true;
    }

    public void stopRunning() {
        running = false;
    }

    public static void setCurrentPlayerIndex(int index) {
        currentPlayerIndex.set(index);
    }

    @Override
    public void run() {
        int index = players.indexOf(this);
        Player currentPlayer;

        while (running && !isGameOver) {
            if (currentPlayerIndex.get() == index) {
                currentPlayer = players.get(index);

                synchronized (currentPlayer) {
                    if (hand.size() == 0) {
                        break;
                    }

                    int nextPlayerIndex = (index + 1) % players.size();
                    Player nextPlayer = players.get(nextPlayerIndex);

                    int randomCardIndex = -1;
                    if (nextPlayer.getHandSize() > 0) {
                        randomCardIndex = new Random().nextInt(nextPlayer.getHandSize());
                    }

                    if (randomCardIndex != -1) {
                        Card randomCard = nextPlayer.hand.remove(randomCardIndex);
                        currentPlayer.receiveCard(randomCard);

                        currentPlayer.discardPair();

                        System.out.println(currentPlayer.name + " took one card from " + nextPlayer.getPName());
                        System.out.println("Card taken: " + randomCard);
                        printCurrentHand();
                    }
                    currentPlayerIndex.set((currentPlayerIndex.get() + 1) % players.size());
                    currentPlayer.notify();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void printCurrentHand() {
        System.out.println(name + "'s Hand:");
        for (Card card : hand) {
            System.out.println(card.toString());
        }
        System.out.println();
    }
}
