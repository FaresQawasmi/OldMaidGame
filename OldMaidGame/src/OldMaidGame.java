import Entities.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class OldMaidGame {
    private static int playerCount;
    private static List<Player> players;

    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();

        players = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Number of Players: ");
        playerCount = scanner.nextInt();
        scanner.nextLine();

        for (int i = 1; i <= playerCount; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String playerName = scanner.nextLine();
            players.add(new Player(playerName));
        }

        int cardsPerPlayer = 53 / playerCount;
        for (Player player : players) {
            List<Card> dealtCards = deck.deal(cardsPerPlayer);
            for (Card card : dealtCards) {
                player.receiveCard(card);
            }
        }

        Collections.shuffle(players);

        Player.initializePlayers(players);

        for (Player player : players) {
            player.start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int currentPlayerIndex = 0;
        Player.setCurrentPlayerIndex(currentPlayerIndex);
        synchronized (players.get(currentPlayerIndex)) {
            players.get(currentPlayerIndex).notify();
        }

        int numPlayersWithCards = playerCount;
        while (numPlayersWithCards > 1) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            Player.setCurrentPlayerIndex(currentPlayerIndex);

            Player currentPlayer = players.get(currentPlayerIndex);

            synchronized (currentPlayer) {
                if (currentPlayer.getHandSize() == 0) {
                    numPlayersWithCards--;
                    players.remove(currentPlayer);
                    continue;
                }
                currentPlayer.notify();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Player.stopGame();
        for (Player player : players) {
            player.stopRunning();
            synchronized (player) {
                player.notify();
            }
        }

        for (Player player : players) {
            try {
                player.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Player loser = null;
        for (Player player : players) {
            if (player.hasJoker()) {
                loser = player;
                break;
            }
        }

        System.out.println("Game over!");
        for (Player player : players) {
            System.out.println("Everyone finished and " + player.getPName() + " has the Joker.....");
        }
        assert loser != null;
        System.out.println(loser.getPName() + " is the loser!");
    }
}
