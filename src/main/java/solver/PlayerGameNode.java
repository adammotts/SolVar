package solver;

import java.util.ArrayList;

public class PlayerGameNode extends GameNode {

    public PlayerGameNode(Integer sumVal, GameNodeValueType type) {
        this.sumVal = sumVal;
        this.valType = type;
        if (sumVal >= 21) {
            this.type = GameNodeType.TERMINAL;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerGameNode) {
            PlayerGameNode other = (PlayerGameNode) o;
            return sumVal == other.sumVal && valType == other.valType;
        }
        return false;
    }

    public static ArrayList<PlayerGameNode> allPlayerNodes() {
        ArrayList<PlayerGameNode> allNodes = new ArrayList<>();

        for (int i = 30; i >= 22; i--) {
            allNodes.add(new PlayerGameNode(i, GameNodeValueType.HARD));
        }
        for (int i = 21; i >= 11; i--) {
            allNodes.add(new PlayerGameNode(i, GameNodeValueType.HARD));
        }

        // Not possible to have soft 11 with 2 cards
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

        allNodes.add(new PlayerGameNode(12, GameNodeValueType.SOFT));

        for (int i = 10; i >= 2; i--) {
            allNodes.add(new PlayerGameNode(i * 2, GameNodeValueType.HARD));
        }

        return allNodes;
    }
}
