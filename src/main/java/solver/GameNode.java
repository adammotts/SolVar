package solver;

import java.util.Objects;

public abstract class GameNode {

    public final Integer sumVal;
    public final GameNodeValueType valType;
    public final GameNodeType type;

    public GameNode(Integer sumVal, GameNodeValueType valType, GameNodeType type) {
        this.sumVal = sumVal;
        this.valType = valType;
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sumVal, this.valType, this.type);
    }

    @Override
    public String toString() {
        return "[" + sumVal + " " + valType + "]";
    }
}
