package solver;

import adammotts.cards.Card;

import java.util.ArrayList;

public class DealerGameNode extends GameNode {

    public DealerGameNode(Integer sumVal, GameNodeValueType valType) {
        super(sumVal, valType, sumVal >= 17 ? GameNodeType.TERMINAL : GameNodeType.ACTIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DealerGameNode) {
            DealerGameNode other = (DealerGameNode) o;
            return sumVal == other.sumVal && valType == other.valType;
        }
        return false;
    }

    public DealerGameNode simulateDealerAddCard(Integer cardVal) {
        int resultSum = this.sumVal + cardVal;
        GameNodeValueType resultType;

        if (cardVal == 11) {
            if (this.valType == GameNodeValueType.MAYBE_BLACKJACK) {
                if (this.sumVal == 10) {
                    resultType = GameNodeValueType.BLACKJACK;
                }
                else {
                    resultSum -= 10;
                    resultType = GameNodeValueType.SOFT;
                }
            }

            // When pulling an ace, correct the sum accordingly. Hard remain hard and soft remain soft
            else if (resultSum > 21) {
                resultSum -= 10;
                resultType = this.valType;
            }

            // Small hard values < 10 will become soft if pulling an ace, 10 will become blackjack
            else {
                resultType = GameNodeValueType.SOFT;
            }
        }

        else {
            if (this.sumVal == 11 && this.valType == GameNodeValueType.MAYBE_BLACKJACK) {
                if (cardVal == 10) {
                    resultType = GameNodeValueType.BLACKJACK;
                }
                else {
                    resultType = GameNodeValueType.HARD;
                }
            }

            else if (this.sumVal == 10 && this.valType == GameNodeValueType.MAYBE_BLACKJACK) {
                resultType = GameNodeValueType.HARD;
            }

            // Without an ace, all hard remain hard
            else if (this.valType == GameNodeValueType.HARD) {
                resultType = GameNodeValueType.HARD;
            }

            // Soft values that exceed 21 become hard, others remain soft
            else {
                if (resultSum > 21) {
                    resultSum -= 10;
                    resultType = GameNodeValueType.HARD;
                }
                else {
                    resultType = GameNodeValueType.SOFT;
                }
            }
        }

        return new DealerGameNode(resultSum, resultType);
    }

    public static ArrayList<DealerGameNode> allDealerNodes() {
        ArrayList<DealerGameNode> allNodes = new ArrayList<>();

        for (int i = 26; i >= 22; i--) {
            allNodes.add(new DealerGameNode(i, GameNodeValueType.HARD));
        }
        allNodes.add(new DealerGameNode(21, GameNodeValueType.BLACKJACK));
        for (int i = 21; i >= 11; i--) {
            allNodes.add(new DealerGameNode(i, GameNodeValueType.HARD));
        }
        for (int i = 21; i >= 12; i--) {
            allNodes.add(new DealerGameNode(i, GameNodeValueType.SOFT));
        }
        allNodes.add(new DealerGameNode(11, GameNodeValueType.MAYBE_BLACKJACK));
        allNodes.add(new DealerGameNode(10, GameNodeValueType.MAYBE_BLACKJACK));
        for (int i = 10; i >= 2; i--) {
            allNodes.add(new DealerGameNode(i, GameNodeValueType.HARD));
        }

        return allNodes;
    }

    public static ArrayList<DealerGameNode> allDealerTerminalNodes() {
        ArrayList<DealerGameNode> allNodes = new ArrayList<>();

        for (DealerGameNode node : allDealerNodes()) {
            if (node.type == GameNodeType.TERMINAL) {
                allNodes.add(node);
            }
        }

        return allNodes;
    }

    public static ArrayList<DealerGameNode> allDealerStartingNodes() {
        ArrayList<DealerGameNode> allNodes = new ArrayList<>();

        for (Integer cardVal : Card.ranks.values()) {
            if (cardVal == 11) {
                allNodes.add(new DealerGameNode(cardVal, GameNodeValueType.MAYBE_BLACKJACK));
            }
            else if (cardVal == 10) {
                allNodes.add(new DealerGameNode(cardVal, GameNodeValueType.MAYBE_BLACKJACK));
            }
            else {
                allNodes.add(new DealerGameNode(cardVal, GameNodeValueType.HARD));
            }
        }

        return allNodes;
    }
}
