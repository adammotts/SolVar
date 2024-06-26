package adammotts.players;

import adammotts.cards.Card;

import java.util.ArrayList;

public abstract class Participant {

    public ArrayList<Card> hand = new ArrayList<>();
    public boolean stand = false;

    public Participant() {}

    public void initialDeal(ArrayList<Card> cards) {
        hand.addAll(cards);
    }

    /**
     * @param card Add a card to the hand
     * @return The resultant sum
     */
    public int hit(Card card) {
        hand.add(card);
        this.printCards();
        return this.computeSum();
    }

    /**
     * @return The resultant sum
     */
    public int stand() {
        this.stand = true;
        return this.computeSum();
    }

    /**
     * @return The most optimal value of the current hand (largest value less than 21)
     */
    public int computeSum() {
        int numAces = Card.countAces(hand);

        int sumVal = 0;
        for (Card handCard : this.hand) {
            sumVal += handCard.value;
        }

        while (numAces > 0 && sumVal > 21) {
            sumVal -= 10;
            numAces--;
        }

        return sumVal;
    }

    public void reset() {
        this.hand.clear();
        this.stand = false;
    }

    /**
     * Print the current player's cards
     */
    public void printCards() {
        StringBuilder output = new StringBuilder();
        output.append("[");
        for (int i = 0; i < this.hand.size(); i++) {
            output.append(this.hand.get(i).toString());
            if (i != this.hand.size() - 1) {
                output.append(", ");
            }
        }
        output.append("] = ");
        output.append(this.computeSum());
        System.out.println(output);
    }
}
