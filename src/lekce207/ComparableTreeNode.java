package lekce207;

import java.util.HashSet;
import java.util.Set;

public class ComparableTreeNode<V extends Comparable<V>> {
    public static void main() {
        ComparableTreeNode<Integer>[] values = new ComparableTreeNode[11];
        for (int i = 0; i < 11; i++) {
            values[i] = new ComparableTreeNode<>(i);
        }
        values[0].addChild(values[1]);
        values[0].addChild(values[2]);
        values[1].addChild(values[3]);
        values[1].addChild(values[4]);
        values[2].addChild(values[5]);
        values[2].addChild(values[6]);
        values[3].addChild(values[7]);
        values[3].addChild(values[8]);
        values[4].addChild(values[9]);
        values[4].addChild(values[10]);
        System.out.println(values[0].maximum());
        System.out.println(values[1].maximum());
        System.out.println(values[2].maximum());
        System.out.println(values[3].maximum());
        System.out.println(values[4].maximum());
        System.out.println(values[5].maximum());
    }
    private final V value;
    private final Set<ComparableTreeNode<V>> children = new HashSet<>();

    public ComparableTreeNode(V value) {
        this.value = value;
    }

    public void addChild(ComparableTreeNode<V> child) {
        children.add(child);
    }

    public V maximum() {
        V maximum = value;
        for (ComparableTreeNode<V> child : children) {
            V childMaximum = child.maximum();
            if (childMaximum.compareTo(maximum) > 0) {
                maximum = childMaximum;
            }
        }
        return maximum;
    }
}
