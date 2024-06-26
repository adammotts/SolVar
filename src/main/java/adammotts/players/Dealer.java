package adammotts.players;

import adammotts.cards.Card;

import java.util.ArrayList;

public class Dealer extends Participant {

    public Card getVisibleCard() {
        return this.hand.get(0);
    }

    /**
     * Play the game (stand on soft/hard 17 or higher) and return final sum
     */
    public int playGame(ArrayList<Card> deck) {
        while (this.computeSum() < 17) {
            this.hit(Card.deal(deck));
        }

        return this.computeSum();
    }
}
