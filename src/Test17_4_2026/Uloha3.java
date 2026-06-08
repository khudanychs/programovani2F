package Test17_4_2026;
//3. Napište tělo statické metody findSecondLargestPrimeNumberLessThan(int number), která najde druhé největší prvočíslo menší než zadané číslo number.
// Pro určení, zda určité číslo je prvočíslem, použijte statickou metodu isPrime(int number), která pro prvočíslo vrací true.
//
//Pokud je počet prvočísel menších než zadané číslo menší, než dva, metoda musí vrátit 0.
//
//Do vývojového prostředí zkopírujte vzorový kód níže. Výsledek pak vložte do editačního pole pod vzorem.
// Vložte pouze tělo metody, jak je naznačeno ve vzorovém příkladu.

class Primes {

    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        int max = (int) Math.sqrt(number);
        for (int i = 2; i <= max; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int findSecondLargestPrimeNumberLessThan(int number) {
        // -------------- zde odstrihnout --------------------------------------
        int count = 0;
        for (int i = number - 1; i >= 2; i--) {
            if (isPrime(i)) {
                count++;
                if (count == 2) {
                    return i;
                }
            }
        }
        return 0;
        // -------------- zde odstrihnout --------------------------------------
    }

    public static void main(String[] args) {
        System.out.println(findSecondLargestPrimeNumberLessThan(2));   // 0
        System.out.println(findSecondLargestPrimeNumberLessThan(10));  // 5
        System.out.println(findSecondLargestPrimeNumberLessThan(100)); // 89
    }
}




public class Uloha3 {
}
