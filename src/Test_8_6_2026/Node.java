package Test_8_6_2026;

import java.util.HashSet;
import java.util.Set;
public class Node {
    public static void main(String[] args) {
        Node red1 = new Node();
        Node red2 = new Node();
        Node red3 = new Node();
        Node red4 = new Node();
        red1.connect(red2);
        red1.connect(red3);
        red2.connect(red4);
        Node blue1 = new Node();
        Node blue2 = new Node();
        Node blue3 = new Node();
        blue1.connect(blue2);
        blue2.connect(blue3);
        blue3.connect(blue1);
        System.out.println(red1.getComponentSize());
        System.out.println(red2.getComponentSize());
        System.out.println(red3.getComponentSize());
        System.out.println(blue1.getComponentSize());
    }
    private final Set<Node> neighborhood = new HashSet<>();
    public void connect(Node node) {
        node.neighborhood.add(this);
        this.neighborhood.add(node);
    }
    public int getComponentSize() {
        // -------------- zde odstrihnout --------------------------------------
        Set<Node> components = new HashSet<>();
        return components.size();
        // -------------- zde odstrihnout --------------------------------------
    }
}
//Následující graf obsahuje jednoduché vrcholy a neorientované hrany
//(tzn., že pokud je vrchol A spojen hranou s vrcholem B, pak dotazem A.neigborhood.contains(B)
//i B.neigborhood.contains(A) obdržíme v obou případech true).
//V ukázkovém kódu je vytvořen graf se dvěma komponentami,
//vrcholy jsou pro jednoduchost pojmenovány podle barev jednotlivých komponent.
//Implementujte metodu, která vyhledá všechny uzly komponenty, do které uzel náleží, a vrátí jejich počet
//(v ukázkovém kódu třikrát 4 a jednou 3).
