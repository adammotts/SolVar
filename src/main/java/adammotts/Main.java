package adammotts;

import solver.Solver;

public class Main {
    public static void main(String[] args) {
        Solver.printDealerTree(Solver.generateDealerStartingTree());
        //Solver.printPlayerTree(Solver.generatePlayerStartingTree());

        /*
        Blackjack game = new Blackjack();
        int stop = 0;
        while (stop != -1) {
            stop = game.startRound();
        }
        */
    }
}