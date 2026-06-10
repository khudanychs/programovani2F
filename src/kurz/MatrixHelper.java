package kurz;

import java.util.Arrays;
import java.util.Objects;

public class MatrixHelper {

    public static final String NUMBER_FORMAT = "%.0f";
    public static final Formatter.Decorator MATRIX_DECORATOR = new Formatter.DecoratorBuilder().decorator();
    public static final Formatter.Decorator VECTOR_DECORATOR = new Formatter.Decorator("; ", "(", ")");
    public static final Formatter.Decorator POINT_DECORATOR = new Formatter.Decorator("; ", "[", "]");

    public static int width(double[]... m) {
        Objects.requireNonNull(m, "null matrix");
        if (m.length == 0) {
            throw new IllegalArgumentException("empty matrix");
        }
        int width = m[0].length;
        if (width == 0) {
            throw new IllegalArgumentException("empty matrix");
        }
        for (int i = 1; i < m.length; i++) {
            if (m[i].length != width) {
                throw new IllegalArgumentException("non-regular matrix");
            }
        }
        return width;
    }

    public static int height(double[]... m) {
        width(m);
        return m.length;
    }

    public static String toString(double[]... m) {
        return new Formatter.Operand(null, m).toString();
    }

    public static String toString(Formatter.Decorator decorator, double[]... m) {
        return new Formatter.Operand(null, m).toString(decorator);
    }

    public static String toString(String numberFormat, double[]... m) {
        return new Formatter.Operand(null, m).toString(numberFormat);
    }

    public static String toString(Formatter.Decorator decorator, String numberFormat, double[]... m) {
        return new Formatter.Operand(null, m).toString(decorator, numberFormat);
    }

    public static Formatter formater() {
        return new Formatter();
    }

    public static class Formatter {

        public static class Decorator {

            private final String delimiter;
            private final String leftSingle;
            private final String leftUpper;
            private final String leftMiddle;
            private final String leftBottom;
            private final String rightSingle;
            private final String rightUpper;
            private final String rightMiddle;
            private final String rightBottom;

            public Decorator(String delimiter, String left, String right) {
                this(delimiter, left, left, left, left, right, right, right, right);
            }

            public Decorator(String delimiter, String leftSingle, String leftUpper, String leftMiddle, String leftBottom, String rightSingle, String rightUpper, String rightMiddle, String rightBottom) {
                this.delimiter = delimiter;
                this.leftSingle = leftSingle;
                this.leftUpper = leftUpper;
                this.leftMiddle = leftMiddle;
                this.leftBottom = leftBottom;
                this.rightSingle = rightSingle;
                this.rightUpper = rightUpper;
                this.rightMiddle = rightMiddle;
                this.rightBottom = rightBottom;
            }

            public String getDelimiter(int height, int width, int row, int space) {
                return (space > 0) && (space < width) ? getDelimiter() : "";
            }

            public String getLeft(int height, int width, int row) {
                if (height == 1) {
                    return getLeftSingle();
                }
                if (row == 0) {
                    return getLeftUpper();
                }
                if (row == height - 1) {
                    return getLeftBottom();
                }
                return getLeftMiddle();
            }

            public String getRight(int height, int width, int row) {
                if (height == 1) {
                    return getRightSingle();
                }
                if (row == 0) {
                    return getRightUpper();
                }
                if (row == height - 1) {
                    return getRightBottom();
                }
                return getRightMiddle();
            }

            public String getDelimiter() {
                return delimiter;
            }

            public String getLeftSingle() {
                return leftSingle;
            }

            public String getLeftUpper() {
                return leftUpper;
            }

            public String getLeftMiddle() {
                return leftMiddle;
            }

            public String getLeftBottom() {
                return leftBottom;
            }

            public String getRightSingle() {
                return rightSingle;
            }

            public String getRightUpper() {
                return rightUpper;
            }

            public String getRightMiddle() {
                return rightMiddle;
            }

            public String getRightBottom() {
                return rightBottom;
            }
        }

        public static class DecoratorBuilder {

            private String delimiter = "  ";
            private String leftSingle = "(";
            //            private String leftUpper  = "┌";
            private String leftUpper = "/";
            //            private String leftMiddle = "│";
            private String leftMiddle = "|";
            //            private String leftBottom = "└";
            private String leftBottom = "\\";
            private String rightSingle = ")";
            //            private String rightUpper = "┐";
            private String rightUpper = "\\";
            //            private String rightMiddle = "│";
            private String rightMiddle = "|";
            //            private String rightBottom = "┘";
            private String rightBottom = "/";

            public DecoratorBuilder() {
            }

            public DecoratorBuilder setDelimiter(String delimiter) {
                this.delimiter = delimiter;
                return this;
            }

            public DecoratorBuilder setLeft(String left) {
                this.leftSingle = left;
                this.leftUpper = left;
                this.leftMiddle = left;
                this.leftBottom = left;
                return this;
            }

            public DecoratorBuilder setRight(String right) {
                this.rightSingle = right;
                this.rightUpper = right;
                this.rightMiddle = right;
                this.rightBottom = right;
                return this;
            }

            public DecoratorBuilder setLeftSingle(String leftSingle) {
                this.leftSingle = leftSingle;
                return this;
            }

            public DecoratorBuilder setLeftUpper(String leftUpper) {
                this.leftUpper = leftUpper;
                return this;
            }

            public DecoratorBuilder setLeftMiddle(String leftMiddle) {
                this.leftMiddle = leftMiddle;
                return this;
            }

            public DecoratorBuilder setLeftBottom(String leftBottom) {
                this.leftBottom = leftBottom;
                return this;
            }

            public DecoratorBuilder setRightSingle(String rightSingle) {
                this.rightSingle = rightSingle;
                return this;
            }

            public DecoratorBuilder setRightUpper(String rightUpper) {
                this.rightUpper = rightUpper;
                return this;
            }

            public DecoratorBuilder setRightMiddle(String rightMiddle) {
                this.rightMiddle = rightMiddle;
                return this;
            }

            public DecoratorBuilder setRightBottom(String rightBottom) {
                this.rightBottom = rightBottom;
                return this;
            }

            public Decorator decorator() {
                return new Decorator(delimiter, leftSingle, leftUpper, leftMiddle, leftBottom, rightSingle, rightUpper, rightMiddle, rightBottom);
            }

        }

        private static char[] spacer(int width) {
            char[] spacer = new char[width];
            Arrays.fill(spacer, ' ');
            return spacer;
        }
        private final Formatter parent;

        private Formatter() {
            this(null);
        }

        private Formatter(Formatter parent) {
            this.parent = parent;
        }

        public Formatter append(String operator) {
            return new Operator(this, operator);
        }

        public Formatter append(double[]... operand) {
            return new Operand(this, operand);
        }

        protected int height() {
            return 0;
        }

        protected int width(String numberFormat) {
            return 0;
        }

        protected void line(StringBuilder sb, int row, int height, Decorator decorator) {
        }

        private int totalHeight() {
            return Math.max(height(), parent == null ? 0 : parent.totalHeight());
        }

        private int totalWidth(String numberFormat) {
            return width(numberFormat) + (parent == null ? 0 : parent.totalWidth(numberFormat));
        }

        private void totalLine(StringBuilder sb, int row, int height, Decorator decorator) {
            if (parent != null) {
                parent.totalLine(sb, row, height, decorator);
            }
            line(sb, row, height, decorator);
        }

        @Override
        public String toString() {
            return toString(MATRIX_DECORATOR, NUMBER_FORMAT);
        }

        public String toString(Decorator decorator) {
            return toString(decorator, NUMBER_FORMAT);
        }

        public String toString(String numberFormat) {
            return toString(MATRIX_DECORATOR, numberFormat);
        }

        public String toString(Decorator decorator, String numberFormat) {
            int height = totalHeight();
            if (height == 0) {
                return "";
            }
            int width = totalWidth(numberFormat);
            if (width == 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < height; row++) {
                totalLine(sb, row, height, decorator);
                sb.append('\n');
            }
            return sb.toString();
        }

        private static class Operator extends Formatter {

            private final String operator;
            private final char[] spacer;

            private Operator(Formatter parent, String operator) {
                super(parent);
                this.operator = operator;
                this.spacer = spacer(operator.length());
            }

            @Override
            protected int height() {
                return 1;
            }

            @Override
            protected int width(String numberFormat) {
                return operator.length();
            }

            @Override
            protected void line(StringBuilder sb, int row, int height, Decorator decorator) {
                if (row == height / 2) {
                    sb.append(operator);
                } else {
                    sb.append(spacer);
                }
            }

        }

        private static class Operand extends Formatter {

            private final int height;
            private final int width;
            private final double[][] operand;
            private final String[][] value;
            private final int[] widths;
            private boolean dirty = true;
            private int totalWidth = 0;
            private char[] spacer;

            private Operand(Formatter parent, double[][] operand) {
                super(parent);
                this.height = MatrixHelper.height(operand);
                this.width = MatrixHelper.width(operand);
                this.operand = operand;
                this.value = new String[height][width];
                this.widths = new int[width];
            }

            @Override
            protected int height() {
                return height;
            }

            @Override
            protected int width(String numberFormat) {
                if (dirty) {
                    for (int row = 0; row < height; row++) {
                        for (int col = 0; col < width; col++) {
                            String s = String.format(numberFormat, operand[row][col]);
                            value[row][col] = s;
                            widths[col] = Math.max(widths[col], s.length());
                        }
                    }
                    for (int col = 0; col < width; col++) {
                        totalWidth += widths[col];
                    }
                    spacer = spacer(totalWidth);
                    dirty = false;
                }
                return totalWidth;
            }

            @Override
            protected void line(StringBuilder sb, int row, int height, Decorator decorator) {
                int min = (height - this.height + 1) / 2;
                int max = min + this.height - 1;
                if ((row < min) || (row > max)) {
                    int r = row < min ? min : max;
                    int w = totalWidth;
                    w += decorator.getLeft(this.height, this.width, r).length();
                    for (int col = 0; col < this.width; col++) {
                        w += decorator.getDelimiter(this.height, this.width, r, col).length();
                    }
                    w += decorator.getRight(this.height, this.width, r).length();
                    sb.append(spacer(w));
                } else {
                    int r = row - min;
                    sb.append(decorator.getLeft(this.height, this.width, r));
                    for (int col = 0; col < this.width; col++) {
                        sb.append(decorator.getDelimiter(this.height, this.width, r, col));
                        String v = value[r][col];
                        int s = widths[col] - v.length();
                        int d = (s + (v.startsWith("-") ? 0 : 1)) / 2;
                        sb.append(spacer, 0, d).append(value[r][col]).append(spacer, 0, s - d);
                    }
                    sb.append(decorator.getRight(this.height, this.width, r));
                }
            }

        }
    }
}