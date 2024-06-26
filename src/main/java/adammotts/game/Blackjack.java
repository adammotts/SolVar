package adammotts.game;

import adammotts.cards.Card;
import adammotts.players.Dealer;
import adammotts.players.Player;
import java.util.Scanner;

import java.util.ArrayList;

public class Blackjack {

    public Player player;
    public Dealer dealer;
    public ArrayList<Card> deck;
    public Scanner scanner;

    public Blackjack() {
        this.player = new Player();
        this.dealer = new Dealer();
        this.scanner = new Scanner(System.in);
    }

    public int startRound() {
        this.resetGame();
        while (this.player.computeSum() < 21 && !this.player.stand && scanner.hasNextLine()) {
            String input = scanner.nextLine();

            switch(input) {
                case "q":
                    return -1;

                case "h":
                    this.player.hit(Card.deal(deck));
                    break;

                case "s":
                    this.player.stand();
                    break;
            }
        }

        this.dealer.printCards();
        int reward = this.player.alertGameOver(this.dealer.playGame(this.deck));
        System.out.println("Reward: " + reward);
        return reward;
    }

    public void resetGame() {
        this.deck = Card.generateDeck(8);
        this.player.reset();
        this.dealer.reset();
        ArrayList<Card> playerCards = new ArrayList<>();
        ArrayList<Card> dealerCards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                playerCards.add(Card.deal(deck));
            }
            else {
                dealerCards.add(Card.deal(deck));
            }
        }

        this.player.initialDeal(playerCards);
        this.dealer.initialDeal(dealerCards);

        this.printStatus();
    }

    /**
     * Print the current status of the game (cards and numbers)
     */
    public void printStatus() {
        System.out.println("Dealer shows: [" + this.dealer.getVisibleCard() + "]");
        System.out.println("You have: ");
        this.player.printCards();
    }
}
