package adammotts.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Card {

    public static HashMap<String, Integer> ranks = new HashMap<String, Integer>() {{
        put("2", 2);
        put("3", 3);
        put("4", 4);
        put("5", 5);
        put("6", 6);
        put("7", 7);
        put("8", 8);
        put("9", 9);
        put("10", 10);
        put("J", 10);
        put("Q", 10);
        put("K", 10);
        put("A", 11);
    }};

    public static HashMap<String, String> suits = new HashMap<String, String>() {{
        put("s", "♠");
        put("h", "♥");
        put("d", "♦");
        put("c", "♣");
    }};

    public String rank;
    public Integer value;

    public Card(String rank) {
        this.rank = rank;
        this.value = ranks.get(rank);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card card = (Card) o;
            return this.value == card.value;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.rank;
    }

    /**
     * @param numDecks Number of decks to be used
     * @return A shuffled deck
     */
    public static ArrayList<Card> generateDeck(int numDecks) {
        ArrayList<Card> deck = new ArrayList<>();

        for (int d = 0; d < numDecks; d++) {
            for (int s = 0; s < suits.size(); s++) {
                for (String r : ranks.keySet()) {
                    deck.add(new Card(r));
                }
            }
        }

        shuffle(deck);

        return deck;
    }

    public static void shuffle(ArrayList<Card> deck) {
        Random random = new Random();
        for (int i = deck.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    public static Card deal(ArrayList<Card> deck) {
        return deck.remove(deck.size() - 1);
    }

    public static int countAces(ArrayList<Card> cards) {
        int numAces = 0;
        for (Card card: cards) {
            if (card.rank.equals("A")) {
                numAces++;
            }
        }

        return numAces;
    }
}
