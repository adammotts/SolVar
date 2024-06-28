package solver;

import java.util.ArrayList;

public class PlayerGameNode extends GameNode {

    public PlayerGameNode(Integer sumVal, GameNodeValueType valType) {
        super(sumVal, valType, sumVal >= 21 ? GameNodeType.TERMINAL : GameNodeType.ACTIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerGameNode) {
            PlayerGameNode other = (PlayerGameNode) o;
            return sumVal == other.sumVal && valType == other.valType;
        }
        return false;
    }

    public PlayerGameNode simulatePlayerAddCard(Integer cardVal) {
        int resultSum = this.sumVal + cardVal;
        GameNodeValueType resultType;

        if (cardVal == 11) {
            if (resultSum > 21) {
                resultSum -= 10;
                resultType = this.valType;
            } else {
                resultType = GameNodeValueType.SOFT;
            }
        } else {
            if (this.valType == GameNodeValueType.HARD) {
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

        return new PlayerGameNode(resultSum, resultType);
    }

    public PlayerGameNode simulatePlayerSplitCards() {
        assert this.valType == GameNodeValueType.SPLITTABLE;

        int splitVal;
        GameNodeValueType splitType;

        if (this.sumVal == 12) {
            splitVal = 11;
            splitType = GameNodeValueType.SOFT;
        }
        else {
            splitVal = this.sumVal / 2;
            splitType = GameNodeValueType.HARD;
        }

        return new PlayerGameNode(splitVal, splitType);
    }

    public PlayerGameNode nonSplittableEquivalent() {
        assert this.valType == GameNodeValueType.SPLITTABLE;

        if (this.sumVal == 12) {
            return new PlayerGameNode(12, GameNodeValueType.SOFT);
        }
        else {
            return new PlayerGameNode(this.sumVal, GameNodeValueType.HARD);
        }
    }

    public PlayerGameNode simulatePlayerAddCardSplittable(Integer cardVal) {
        int resultSum = this.sumVal + cardVal;
        GameNodeValueType resultType;

        if (this.sumVal == cardVal) {
            if (resultSum > 21) {
                resultSum -= 10;
            }
            resultType = GameNodeValueType.SPLITTABLE;
        }
        else if (cardVal == 11) {
            if (resultSum > 21) {
                resultSum -= 10;
                resultType = this.valType;
            } else {
                resultType = GameNodeValueType.SOFT;
            }
        } else {
            if (this.valType == GameNodeValueType.HARD) {
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

        return new PlayerGameNode(resultSum, resultType);
    }

    public static ArrayList<PlayerGameNode> allPlayerNonSplitNodes() {
        ArrayList<PlayerGameNode> allNodes = new ArrayList<>();

        for (int i = 30; i >= 22; i--) {
            allNodes.add(new PlayerGameNode(i, GameNodeValueType.HARD));
        }

        allNodes.add(new PlayerGameNode(21, GameNodeValueType.BLACKJACK));

        for (int i = 21; i >= 11; i--) {
            allNodes.add(new PlayerGameNode(i, GameNodeValueType.HARD));
        }

        // Not possible to have soft 11 with 2 cards. Include soft 12 to delegate splits to
        for (int i = 21; i >= 12; i--) {
            allNodes.add(new PlayerGameNode(i, GameNodeValueType.SOFT));
        }

        // Not possible to have hard 2 or hard 3 with 2 cards
        for (int i = 10; i >= 4; i--) {
            allNodes.add(new PlayerGameNode(i, GameNodeValueType.HARD));
        }

        return allNodes;
    }

    public static ArrayList<PlayerGameNode> allPlayerSplitNodes() {
        ArrayList<PlayerGameNode> allNodes = new ArrayList<>();

        allNodes.add(new PlayerGameNode(12, GameNodeValueType.SPLITTABLE));

        for (int i = 10; i >= 2; i--) {
            allNodes.add(new PlayerGameNode(i * 2, GameNodeValueType.SPLITTABLE));
        }

        return allNodes;
    }
}
