package lekce207;

public abstract class SimpleDoubleNode {
    public static void main(String[] args) {
        SimpleDoubleNode left = new Operator(Operator.OperatorType.PLUS, new Constant(1.0), new Constant(3.0)); //+ 1 3
        SimpleDoubleNode right = new Operator(Operator.OperatorType.MINUS, new Constant(7.0), new Constant(5.0)); // - 7 5
        SimpleDoubleNode root = new Operator(Operator.OperatorType.TIMES,  left, right); // * + 1 3 - 7 5
        System.out.println(root.compute());
    }
    public abstract double compute();

public static class Constant extends SimpleDoubleNode {
    private final double value;
    public Constant(double value) {
        this.value = value;
    }
    @Override
    public double compute() {
        return value;
    }
}
public static class Operator extends SimpleDoubleNode {
    public enum OperatorType {
        PLUS{
            @Override
            public double compute(SimpleDoubleNode left, SimpleDoubleNode right) {
                return left.compute() + right.compute();
            }
        },
        MINUS{
            @Override
            public double compute(SimpleDoubleNode left, SimpleDoubleNode right) {
                return left.compute() - right.compute();
            }
        },
        TIMES{
            @Override
            public double compute(SimpleDoubleNode left, SimpleDoubleNode right) {
                return left.compute() * right.compute();
            }
        },
        DIVIDE{
            @Override
            public double compute(SimpleDoubleNode left, SimpleDoubleNode right) {
                return left.compute() / right.compute();
            }
        };
        public abstract double compute(SimpleDoubleNode left, SimpleDoubleNode right);
    }
    private final SimpleDoubleNode left;
    private final SimpleDoubleNode right;
    private final OperatorType type;
    public Operator( OperatorType type, SimpleDoubleNode left, SimpleDoubleNode right) {
        this.left = left;
        this.right = right;
        this.type = type;
    }
    @Override
    public double compute() {
        return type.compute(left, right);
    }
}

}
