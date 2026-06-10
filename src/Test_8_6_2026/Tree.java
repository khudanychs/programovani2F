package Test_8_6_2026;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
public class Tree {
    public static void main(String[] args) {
        Tree leaf1 = new Tree(10);
        Tree leaf2 = new Tree(20);
        Tree leaf3 = new Tree(30);
        Tree branch1 = new Tree(-50, leaf1, leaf2);
        Tree branch2 = new Tree(15, leaf3);
        Tree branch3 = new Tree(25, branch2);
        Tree root = new Tree(0, branch1, branch3);
        System.out.println(root.getMaxValue());
        System.out.println(branch1.getMaxValue());
    }
    private final double value;
    private final Set<Tree> children = new HashSet<>();
    public Tree(double value) {
        this.value = value;
    }
    public Tree(double value, Tree... children) {
        this.value = value;
        this.children.addAll(Arrays.asList(children));
    }
    public double getMaxValue() {

        // -------------- zde odstrihnout --------------------------------------
        double max = value;
        for (Tree child : children) {
            max = Math.max(max, child.getMaxValue());
        }
        return max;


        // -------------- zde odstrihnout --------------------------------------
    }
}
//Následující strom má v každém svém vrcholu hodnotu typu double. Implementujte metodu,
//která najde a vrátí největší hodnotu ze všech uzlů příslušného podstromu (tzn., že nehledá u rodičů a sourozenců).
//Správná implementace by měla po spuštění ukázkového kódu vypsat hodnoty 30.0 a 20.0.
