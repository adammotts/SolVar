package adammotts;

import adammotts.cards.Card;
import solver.Solver;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        //Solver.printDealerTree(Solver.generateDealerStartingTree());
        //Solver.printPlayerTree(Solver.generatePlayerStartingTree());

        ArrayList<Card> playerCards = new ArrayList<Card>() {{
            add(new Card("3"));
            add(new Card("4"));
        }};

        Card dealerCard = new Card("10");

        System.out.println("Best move for [" + playerCards.get(0) + ", " + playerCards.get(1) + "] against dealer " + dealerCard + " is: " + Solver.getBestMove(playerCards, dealerCard));

        /*
        Blackjack game = new Blackjack();
        int stop = 0;
        while (stop != -1) {
            stop = game.startRound();
        }
        */
    }
}