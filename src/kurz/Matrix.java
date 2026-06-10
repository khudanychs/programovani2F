package kurz;

import java.awt.geom.Point2D;

public class Matrix {
    public static void main(String[] args) {
        Matrix t1 = translate2D(-1, -1);
        Matrix r = rotate2D(Math.PI/4);
        Matrix t2 = translate2D(1, 1);
        Matrix m = t2.multiply(r).multiply(t1);
        System.out.println(m.multiply(point2D(1, 1)));
    }

    public static Matrix point2D(double x, double y) {
        return new Matrix(3, 1,x,y, 1);
    }

    public static Matrix scale2D(double sx, double sy) {
        return new Matrix(3,3,
                sx, 0, 0,
                0, sy, 0,
                0, 0, 1);
    }
    public static Matrix scale2D(double s) {
        return scale2D(s, s);
    }
    public static Matrix translate2D(double tx, double ty) {
        return new Matrix(3,3,
                1, 0, tx,
                 0, 1, ty,
                 0, 0, 1);
    }
    public static Matrix translate2D(double t) {
        return translate2D(t, t);
    }
    public static Matrix rotate2D(double angle) {
        return new Matrix(3,3,
                Math.cos(angle), -Math.sin(angle), 0,
                Math.sin(angle), Math.cos(angle), 0,
                0, 0, 1);
    }
    private final int rows;
    private final int cols;
    private final double[][] value;

    public Matrix(int rows, int cols, double... values) {
        this.rows = rows;
        this.cols = cols;
        this.value = new double[rows][cols];
        for (int i = 0; i< values.length; i++){
            this.value[i/cols][i%cols] = values[i];
        }
    }

    public double get(int row, int col) {
        return value[row][col];
    }

    public Matrix multiply(double scalar) {
        Matrix result = new Matrix(this.rows, this.cols);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                result.value[row][col] = this.value[row][col] * scalar;
            }
        }
        return result;
    }
    public Matrix multiply(Matrix other) {
        if (this.cols != other.rows) {
            throw  new ArithmeticException("Nelze nasobit matice");
        }
        Matrix n = new Matrix(this.rows, other.cols);
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < other.cols; col++) {
                double sum = 0;
                for (int i = 0; i < this.cols; i++) {
                    sum += this.value[row][i] * other.value[i][col];
                }
                n.value[row][col] = sum;
            }
        }
        return n;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            sb.append("( ");
            for (int col = 0; col < cols; col++) {
                sb.append(value[row][col]);
                sb.append(" ");
            }
            sb.append(")\n");
        }
        return sb.toString();
    }
}
