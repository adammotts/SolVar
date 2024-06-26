package solver;

import adammotts.cards.Card;

import java.util.ArrayList;

public class DealerGameNode extends GameNode {

    public DealerGameNode(Integer sumVal, GameNodeValueType type) {
        this.sumVal = sumVal;
        this.valType = type;
        if (sumVal >= 17) {
            this.type = GameNodeType.TERMINAL;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DealerGameNode) {
            DealerGameNode other = (DealerGameNode) o;
            return sumVal == other.sumVal && valType == other.valType;
        }
        return false;
    }

    public static ArrayList<DealerGameNode> allDealerNodes() {
        ArrayList<DealerGameNode> allNodes = new ArrayList<>();

        for (int i = 26; i >= 22; i--) {
            allNodes.add(new DealerGameNode(i, GameNodeValueType.HARD));
        }
        for (int i = 21; i >= 11; i--) {
            allNodes.add(new DealerGameNode(i, GameNodeValueType.HARD));
        }
        for (int i = 21; i >= 11; i--) {
            allNodes.add(new DealerGameNode(i, GameNodeValueType.SOFT));
        }
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
                allNodes.add(new DealerGameNode(cardVal, GameNodeValueType.SOFT));
            }
            else {
                allNodes.add(new DealerGameNode(cardVal, GameNodeValueType.HARD));
            }
        }

        return allNodes;
    }
}
