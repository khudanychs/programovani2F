package Test17_4_2026;

class Digit {

    public static int reverse(int number) {
        int reversed = 0;

        while (number != 0) {
            int digit = number % 10;
            reversed = reversed * 10 + digit;
            number /= 10;
        }

        return reversed;
    }

    public static void main(String[] args) {
        System.out.println(reverse(123));
        System.out.println(reverse(10005));
        System.out.println(reverse(-123));
        System.out.println(reverse(-10005));
    }
}

public class Uloha4 {
}
