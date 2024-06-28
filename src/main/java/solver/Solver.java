package solver;

import adammotts.cards.Card;

import java.util.ArrayList;
import java.util.HashMap;

public class Solver {

    /**
     * @return The probability of reaching various terminal nodes given a starting value (including a single card)
     *
     * Nodes: 2, 3, 4, 5, 6, 7, 8, 9, 10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20, s21,
     * 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
     *
     * Terminal Nodes: s17, s18, s19, s20, s21, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
     */
    public static HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> generateDealerTree() {
        // Map of all nodes to the probability of attaining each of the various terminal nodes
        HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> dealerTree = new HashMap<>();

        // All terminal nodes and the probabilities of attaining them
        HashMap<DealerGameNode, Double> terminalNodeProbabilities = new HashMap<>();

        for (DealerGameNode node : DealerGameNode.allDealerTerminalNodes()) {
            terminalNodeProbabilities.put(node, 0.0);
        }

        for (DealerGameNode node : DealerGameNode.allDealerNodes()) {
            HashMap<DealerGameNode, Double> probabilities = new HashMap<>(terminalNodeProbabilities);

            // All terminal nodes have a 100% chance of reaching a terminal node (themselves)
            if (node.type == GameNodeType.TERMINAL) {
                probabilities.put(node, 1.0);
            }

            else {
                ArrayList<Integer> allValues = new ArrayList<>(Card.ranks.values());
                assert allValues.size() == 13;

                // Compute the probability of reaching the various terminal nodes based on the combination of all nodes that you can reach
                for (Integer cardVal : allValues) {
                    DealerGameNode resultAfterAddCard = node.simulateDealerAddCard(cardVal);

                    HashMap<DealerGameNode, Double> resultNodeProbabilities = dealerTree.get(resultAfterAddCard);

                    for (DealerGameNode terminalNode : resultNodeProbabilities.keySet()) {
                        probabilities.put(
                                terminalNode,
                                probabilities.get(terminalNode) + (resultNodeProbabilities.get(terminalNode) / allValues.size())
                        );
                    }
                }
            }

            dealerTree.put(node, probabilities);
        }

        return dealerTree;
    }

    public static HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> generateDealerStartingTree() {
        HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> dealerTree = generateDealerTree();
        HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> startingTree = new HashMap<>();

        for (DealerGameNode node : DealerGameNode.allDealerStartingNodes()) {
            startingTree.put(node, dealerTree.get(node));
        }

        return startingTree;
    }

    public static void printDealerTree(HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> dealerTree) {
        for (DealerGameNode node : dealerTree.keySet()) {
            System.out.println(node + " {");
            for (DealerGameNode terminalNode : dealerTree.get(node).keySet()) {
                System.out.println("\t" + terminalNode + ": " + dealerTree.get(node).get(terminalNode));
            }
            System.out.println("}");
        }
    }

    /**
     * @return The expected value of various actions given a starting value (not including single cards) and
     * the dealer's card
     */
    public static HashMap<DealerGameNode, HashMap<PlayerGameNode, HashMap<GameAction, Double>>> generatePlayerTree() {

        // The dealer's tree
        HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> dealerTree = generateDealerStartingTree();

        // Strategy tree given dealer's face up card
        HashMap<DealerGameNode, HashMap<PlayerGameNode, HashMap<GameAction, Double>>> strategyTree = new HashMap<>();

        // Player's strategy tree for a single face up card
        HashMap<PlayerGameNode, HashMap<GameAction, Double>> playerTree = new HashMap<>();

        // All possible actions and their expected values
        HashMap<GameAction, Double> actionExpectedValues = new HashMap<>();

        for (GameAction action : GameAction.values()) {
            actionExpectedValues.put(action, 0.0);
        }

        // Make a new player strategy tree for every single possible dealer starting node
        for (DealerGameNode dealerNode : dealerTree.keySet()) {
            HashMap<PlayerGameNode, HashMap<GameAction, Double>> singlePlayerTree = new HashMap<>(playerTree);

            // Populate one instance of the player strategy tree given a single face up dealer card
            for (PlayerGameNode playerNode : PlayerGameNode.allPlayerNodes()) {
                HashMap<GameAction, Double> expectedValues = new HashMap<>(actionExpectedValues);

                // Initialize EVs for all terminal nodes (all actions are same EV)
                if (playerNode.type == GameNodeType.TERMINAL) {
                    double ev = computeExpectedValuePlayerStand(playerNode, dealerNode);
                    for (GameAction action : expectedValues.keySet()) {
                        expectedValues.put(action, ev);
                    }
                }
                else {
                    ArrayList<Integer> allValues = new ArrayList<>(Card.ranks.values());
                    assert allValues.size() == 13;

                    // Compute the EV of a stand
                    expectedValues.put(
                            GameAction.STAND,
                            computeExpectedValuePlayerStand(playerNode, dealerNode)
                    );

                    // Compute the EV of a hit and double
                    for (Integer cardVal : allValues) {
                        PlayerGameNode resultAfterAddCard = playerNode.simulatePlayerAddCard(cardVal);

                        HashMap<GameAction, Double> resultActionExpectedValues = singlePlayerTree.get(resultAfterAddCard);

                        double evBestMove = -Double.MAX_VALUE;

                        for (GameAction action : resultActionExpectedValues.keySet()) {
                            // Can only factor in best moves not including split or double
                            if (resultActionExpectedValues.get(action) > evBestMove && action != GameAction.SPLIT && action != GameAction.DOUBLE) {
                                evBestMove = resultActionExpectedValues.get(action);
                            }
                        }

                        // The EV of a hit is determined by the probability of pulling a single card and the max EV action (not including split or double) of the resulting sum
                        expectedValues.put(
                                GameAction.HIT,
                                expectedValues.get(GameAction.HIT) + (evBestMove / allValues.size())
                        );

                        // The EV of a double is determined by 2x the probability of pulling a single card and the EV of a stand
                        expectedValues.put(
                                GameAction.DOUBLE,
                                expectedValues.get(GameAction.DOUBLE) + (2 * resultActionExpectedValues.get(GameAction.STAND) / allValues.size())
                        );
                    }
                }

                singlePlayerTree.put(playerNode, expectedValues);

            }

            // Loop through and populate the split EVs now that we have the hit, stand, and double EVs
            for (PlayerGameNode splitPlayerNode : PlayerGameNode.allPlayerSplitNodes()) {
                int splitVal;
                GameNodeValueType splitType;

                if (splitPlayerNode.sumVal == 12 && splitPlayerNode.valType == GameNodeValueType.SOFT) {
                    splitVal = 11;
                    splitType = GameNodeValueType.SOFT;
                }
                else {
                    splitVal = splitPlayerNode.sumVal / 2;
                    splitType = GameNodeValueType.HARD;
                }

                ArrayList<Integer> allValues = new ArrayList<>(Card.ranks.values());
                assert allValues.size() == 13;

                double evAssumingSplitIsBestMove = 0;
                double evAssumingSplitIsNotBestMove = 0;

                // No Blackjack possible if split
                for (Integer cardVal : allValues) {
                    int resultSum = splitVal + cardVal;
                    GameNodeValueType resultType;

                    if (cardVal == 11) {
                        if (resultSum > 21) {
                            resultSum -= 10;
                            resultType = splitType;
                        } else {
                            resultType = GameNodeValueType.SOFT;
                        }
                    } else {
                        if (splitType == GameNodeValueType.HARD) {
                            resultType = GameNodeValueType.HARD;
                        } else {
                            if (resultSum > 21) {
                                resultSum -= 10;
                                resultType = GameNodeValueType.HARD;
                            } else {
                                resultType = GameNodeValueType.SOFT;
                            }
                        }
                    }

                    HashMap<GameAction, Double> resultActionExpectedValues = singlePlayerTree.get(
                            new PlayerGameNode(resultSum, resultType)
                    );

                    double evBestMove = -Double.MAX_VALUE;

                    for (GameAction action : resultActionExpectedValues.keySet()) {
                        // Can only factor in best moves not including split
                        if (resultActionExpectedValues.get(action) > evBestMove && action != GameAction.SPLIT) {
                            evBestMove = resultActionExpectedValues.get(action);
                        }
                    }

                    // If the split and new card leads to another split
                    if (resultSum == splitPlayerNode.sumVal) {
                        evAssumingSplitIsNotBestMove += (2 * evBestMove / allValues.size());
                    }
                    else {
                        evAssumingSplitIsBestMove += (2 * evBestMove / (allValues.size() - 2));
                        evAssumingSplitIsNotBestMove += (2 * evBestMove / allValues.size());
                    }
                }

                double evSplit;

                // If split is in fact the best move
                if (evAssumingSplitIsBestMove > evAssumingSplitIsNotBestMove) {
                    evSplit = evAssumingSplitIsBestMove;
                }
                else {
                    evSplit = evAssumingSplitIsNotBestMove;
                }

                singlePlayerTree.get(splitPlayerNode).put(
                        GameAction.SPLIT,
                        evSplit
                );
            }

            strategyTree.put(dealerNode, singlePlayerTree);
        }

        return strategyTree;
    }

    public static double computeExpectedValuePlayerStand(PlayerGameNode playerNode, DealerGameNode dealerNode) {

        HashMap<DealerGameNode, HashMap<DealerGameNode, Double>> dealerTree = generateDealerStartingTree();

        int playerStandVal = playerNode.sumVal;
        double ev = 0;
        double multiplier = 1.0;

        if (playerStandVal == 21 && playerNode.valType == GameNodeValueType.BLACKJACK) {
            multiplier = 1.5;
        }

        HashMap<DealerGameNode, Double> dealerTreeCard = dealerTree.get(dealerNode);
        for (DealerGameNode terminalDealerNode : dealerTreeCard.keySet()) {
            double frequency = dealerTreeCard.get(terminalDealerNode);

            // Blackjack
            if (playerNode.valType == GameNodeValueType.BLACKJACK) {
                if (terminalDealerNode.valType == GameNodeValueType.BLACKJACK) {
                    ev += 0;
                }
                else {
                    ev += multiplier * frequency;
                }
            }

            // Player bust
            else if (playerStandVal > 21) {
                ev -= multiplier * frequency;
            }

            // Dealer bust
            else if (terminalDealerNode.sumVal > 21) {
                ev += multiplier * frequency;
            }

            // Dealer beats player
            else if (terminalDealerNode.sumVal > playerStandVal) {
                ev -= multiplier * frequency;
            }

            // Player beats dealer
            else if (playerStandVal > terminalDealerNode.sumVal) {
                ev += multiplier * frequency;
            }
            else {
                ev += 0;
            }
        }

        return ev;
    }

    public static HashMap<DealerGameNode, HashMap<PlayerGameNode, HashMap<GameAction, Double>>> generatePlayerStartingTree() {
        HashMap<DealerGameNode, HashMap<PlayerGameNode, HashMap<GameAction, Double>>> playerTree = generatePlayerTree();
        HashMap<DealerGameNode, HashMap<PlayerGameNode, HashMap<GameAction, Double>>> startingTree = new HashMap<>();

        for (DealerGameNode node : playerTree.keySet()) {
            HashMap<PlayerGameNode, HashMap<GameAction, Double>> playerStrategyTree = playerTree.get(node);
            startingTree.put(node, new HashMap<>());
            for (PlayerGameNode playerNode : playerStrategyTree.keySet()) {
                if (playerNode.type != GameNodeType.TERMINAL || playerNode.sumVal == 21) {
                    startingTree.get(node).put(playerNode, playerStrategyTree.get(playerNode));
                }
            }
        }

        return startingTree;
    }

    public static void printPlayerTree(HashMap<DealerGameNode, HashMap<PlayerGameNode, HashMap<GameAction, Double>>> playerTree) {
        for (DealerGameNode node : playerTree.keySet()) {
            System.out.println(node + " {");
            for (PlayerGameNode playerNode : playerTree.get(node).keySet()) {
                System.out.println("\t" + playerNode + " {");
                for (GameAction action : playerTree.get(node).get(playerNode).keySet()) {
                    System.out.println("\t\t" + action + ": " + playerTree.get(node).get(playerNode).get(action));
                }
                System.out.println("\t}");
            }
            System.out.println("}");
        }
    }
}
