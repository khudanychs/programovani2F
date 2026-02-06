package lekce205;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Supplier;

public class Prime {

    public static void main(String[] args) {
        int limit = 1_000_000;

        System.out.println("--- HLEDÁNÍ PRVOČÍSEL DO " + limit + " ---");

        // 1. O(1) - Konstantní (Podvod)
        measureAndPrint("1. O(1) - Hardcoded", () -> countConstant(limit));

        // 2. O(n log log n) - Eratosthenovo síto
        measureAndPrint("2. O(n log log n) - Eratosthenovo síto", () -> countSieve(limit));

        // 3. O(n * sqrt(n)) - Optimalizované dělení
        measureAndPrint("3. O(n * sqrt(n)) - Odmocnina", () -> countTrialDivisionSqrt(limit));

        // 4. O(n^2) - Naivní dělení (Velmi pomalé)
        System.out.println("\n--- ZPOMALENÉ METODY (Menší limit: 50 000) ---");
        measureAndPrint("4. O(n^2) - Naivní", () -> countTrialDivisionNaive(100_000));

        // 5. O(n!) - Wilsonova věta
        System.out.println("\n--- EXTRÉMNÍ METODA (Limit jen 100) ---");
        measureAndPrint("5. O(n!) - Wilsonova věta", () -> countWilsonsTheorem(1000));
    }

    // --- TOTO JE TA ZMĚNA ---
    // Tato metoda teď spustí výpočet, změří čas A VYPÍŠE VÝSLEDEK
    private static void measureAndPrint(String name, Supplier<Integer> algorithm) {
        System.out.println("Spouštím: " + name + "...");

        long start = System.currentTimeMillis();
        int result = algorithm.get(); // Tady se provede výpočet a uložíme si počet
        long end = System.currentTimeMillis();

        System.out.println("   -> VÝSLEDEK: Našlo se " + result + " prvočísel");
        System.out.println("   -> ČAS: " + (end - start) + " ms\n");
    }

    // --- ALGORITMY (Stejné jako předtím) ---

    // 1. Konstantní
    public static int countConstant(int n) {
        if (n == 1_000_000) return 78498;
        return -1;
    }

    // 2. Eratosthenovo síto
    public static int countSieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;

        for (int i = 2; i * i <= n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        int count = 0;
        for (boolean b : isPrime) {
            if (b) count++;
        }
        return count;
    }

    // 3. Odmocnina
    public static int countTrialDivisionSqrt(int n) {
        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (isPrimeSqrt(i)) count++;
        }
        return count;
    }

    private static boolean isPrimeSqrt(int num) {
        if (num <= 1) return false;
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // 4. Naivní (n^2)
    public static int countTrialDivisionNaive(int n) {
        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (isPrimeNaive(i)) count++;
        }
        return count;
    }

    private static boolean isPrimeNaive(int num) {
        // Chyba efektivity: jdeme až do num - 1
        for (int i = 2; i < num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // 5. Wilsonova věta (Faktoriál)
    public static int countWilsonsTheorem(int n) {
        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (checkWilson(i)) count++;
        }
        return count;
    }

    private static boolean checkWilson(int p) {
        BigInteger factorial = BigInteger.ONE;
        for (int i = 1; i < p; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        return factorial.add(BigInteger.ONE)
                .mod(BigInteger.valueOf(p))
                .equals(BigInteger.ZERO);
    }
}