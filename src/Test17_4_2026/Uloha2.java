package Test17_4_2026;
//2. Ve třídě Convertor vytvořte metodu convert(int value), která převede předanou celočíselnou kladnou hodnotu do určené číselné soustavy.
// Číselná soustava je určena uspořádaným výčtem cifer předaným konstruktoru v podobě pole znaků (char[]), např. pole {'0', '1'} představuje dvojkovou soustavu.
// Pole udržuje třída v privátní proměnné digits.
//
//Do vývojového prostředí zkopírujte vzorový kód níže. Výsledek pak vložte do editačního pole pod vzorem.
// Vložte pouze tělo metody, jak je naznačeno ve vzorovém příkladu.
//
//Bonus: Vyřešte úlohu i pro záporné a nulové hodnoty.
// Záporná hodnota v číselné soustavě začíná znakem '-' a pokračuje stejnými znaky, jako její absolutní hodnota.



class Convertor {

    private final char[] digits;

    public Convertor(char[] digits) {
        if (digits.length < 2) {
            throw new IllegalArgumentException();
        }
        this.digits = digits;
    }

    public String convert(int value) {
        // -------------- zde odstrihnout --------------------------------------
        String result = digits.length + " ";
        StringBuilder sb = new StringBuilder();
        long kladne = Math.abs((long) value);
        while (kladne > 0) {
            sb.append(digits[(int) (kladne % digits.length)]);
            kladne /= digits.length;
        }
        if ((long) value < 0) {
            result = "-" + String.valueOf(digits[digits.length - 1 - Math.abs(value)]);
        }
        if ((long)value == 0) {
            result = String.valueOf(digits[0]);
        }
        result = sb.reverse().toString();
        return result;
        // -------------- zde odstrihnout --------------------------------------
    }

    private static void test(char[] digits, int value, String check) {
        String result = new Convertor(digits).convert(value);
        System.out.println(value + "[" + String.valueOf(digits) + "] = " + result
                + (check.equals(result) ? " OK" : " BAD " + check));
    }

    public static void main(String[] args) {
        test(new char[]{'0', '1'}, 20, "10100");
        test(new char[]{'0', '1', '2', '3', '4', '5', '6', '7'}, 63, "77");
        test(new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}, 1234, "1234");
    }
}
public class Uloha2 {
}
