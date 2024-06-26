package solver;

public abstract class GameNode {

    public Integer sumVal;
    public GameNodeValueType valType;
    public GameNodeType type;

    @Override
    public int hashCode() {
        return sumVal + valType.hashCode();
    }

    @Override
    public String toString() {
        return "[" + sumVal + " " + valType + "]";
    }
}
