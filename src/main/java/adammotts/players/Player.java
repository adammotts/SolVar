package adammotts.players;

public class Player extends Participant {

    public int stack = 0;

    public int alertGameOver(int dealerValue) {
        int playerSum = this.computeSum();
        int outcome = 0;

        // Player bust
        if (playerSum > 21) {
            outcome = -10;
        }

        // Dealer bust
        else if (dealerValue > 21) {
            outcome = 10;
        }

        // Dealer win
        else if (dealerValue > playerSum) {
            outcome = -10;
        }

        // Player win
        else if (dealerValue < playerSum) {
            outcome = 10;
        }

        this.stack += outcome;
        this.reset();

        return outcome;
    }
}
