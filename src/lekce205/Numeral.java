package lekce205;

import java.util.Arrays;

public class Numeral {
    public static String toString(long n, int base) {
        StringBuilder sb = new StringBuilder();
        while (n != 0) {
            long fraction = n % base;
            char c = fraction < 10 ? '0' : 'A' - 10;
            c+= (char) fraction;
            sb.insert(0, c);
            n /= base;

        }
        return sb.toString();
    }
    public static double horner(double x, double [] a) {
        double value = a[a.length - 1];
        for (int i = a.length - 2; i >= 0; i--) {
            value = a[i] + x * value;
        }
        return value;
    }
    public static double[] add (double[] a, double[] b){
        double[] c = new double[Math.max(a.length, b.length)];
        for (int i = 0; i < c.length; i++) {
            c[i] = (i < a.length ? a[i] : 0) + (i < b.length ? b[i] : 0);
        }
        return c;

    }
    public static int gcd(int a, int b) {
        if (a<=0 || b<=0){
            throw new IllegalArgumentException();
        }
        while (b!=0) {
            int c = a % b;
            a = b;
            b = c;
        }
        return a;
    }
    public static int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }
//    public static void coprimes(int max){
//        for (int i = 2; i <= max; i++) {
//            if (gcd(i, max) == 1) System.out.format("%d", "%d", i);
//        }
//    }
    public static void main(String[] args) {
        System.out.println(toString(245477548, 2));
        System.out.printf("%.2f%n", horner(3, new double[]{1, 2, 5, 2.6}));
        System.out.println(Arrays.toString(add(new double[]{1, 2, 3}, new double[]{1, 2, 3})));
        System.out.println(gcd(36, 48));
        System.out.println(lcm(120, 48));
//        System.out.println(coprimes(100));
    }
}
