package lekce205;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DU_Prvocisla {
    private static String nsToMs(long nanoseconds) {
        return nanoseconds + " ns (" + (nanoseconds / 1_000_000) + " ms)";
    }
    public static void main(String[] args) {
        int[] limity;
        if (args.length > 0) {
            limity = new int[args.length];
            for (int i = 0; i < args.length; i++) {
                limity[i] = Integer.parseInt(args[i]);
            }
        } else {
            limity = new int[]{10_000_000};
        }
        for (int horniHranice : limity) {
            System.out.println("2 až " + horniHranice);

            // SITO
            long startSito = System.nanoTime();
            int pocetSito = eratosthenovoSito(horniHranice);
            long konecSito = System.nanoTime();
            System.out.println("Eratosthenovo sito: " + pocetSito + " za " + nsToMs(konecSito - startSito));

            // POSTUPKA
            long startPostupne = System.nanoTime();
            int pocetPostupne = postupnyTest(horniHranice);
            long konecPostupne = System.nanoTime();
            System.out.println("Postupné testy: " + pocetPostupne + " za " + nsToMs(konecPostupne - startPostupne));

            // VLAKNA
            long startVlakna = System.nanoTime();
            int pocetVlakna = testVlakna(horniHranice);
            long konecVlakna = System.nanoTime();
            System.out.println("Celkem ve vláknech: " + pocetVlakna + " za " + nsToMs(konecVlakna - startVlakna));

            // KONTROLA
            if (pocetSito == pocetPostupne && pocetPostupne == pocetVlakna) {
                System.out.println("Všechny tři metody dávají stejné výsledky.");
            } else {
                System.err.println("Všechny tři metody nedávají stejné výsledky.");
            }
        }
    }

    // Er. SITO
    public static int eratosthenovoSito(int horniHranice) {
        boolean[] prvocisla = new boolean[horniHranice + 1];

        Arrays.fill(prvocisla, true);
        prvocisla[0] = false;
        prvocisla[1] = false;
        int konecKontroly = (int) Math.sqrt(horniHranice);

        for (int i = 2; i <= konecKontroly; i++) {
            if (prvocisla[i]) {
                for (int nasobek = i * i; nasobek <= horniHranice; nasobek += i) {
                    prvocisla[nasobek] = false;
                }
            }
        }

        int pocetPrvocisel = 0;
        for (int i = 2; i <= horniHranice; i++) {
            if (prvocisla[i]) {
                pocetPrvocisel++;
            }
        }
        return pocetPrvocisel;
    }

    // K POSTUPCE
    public static boolean jePrvocislo(int n) {
        if (n < 2) return false;
        int konecKontroly = (int) Math.sqrt(n);
        for (int i = 2; i <= konecKontroly; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
    // POSTUPKA
    public static int postupnyTest(int horniHranice) {
        int pocetPrvocisel = 0;
        for (int i = 2; i <= horniHranice; i++) {
            if (jePrvocislo(i)) {
                pocetPrvocisel++;
            }
        }
        return pocetPrvocisel;
    }

    // VLAKNA
    public static int testVlakna(int horniHranice) {
        System.out.println("Rozdělení do vláken:");

        int pocetVlaken = Runtime.getRuntime().availableProcessors();
        List<Thread> seznamVlaken = new ArrayList<>();
        AtomicInteger celkovyPocet = new AtomicInteger(0);
        int velikostIntervalu = horniHranice / pocetVlaken;

        for (int i = 0; i < pocetVlaken; i++) {
            final int start = (i * velikostIntervalu) + 1;
            final int end = (i == pocetVlaken - 1) ? horniHranice : (start + velikostIntervalu - 1);
            final int skutecnyStart = (start < 2) ? 2 : start;

            Thread vlakno = new Thread(() -> {
                int prozatimniPocet = 0;
                for (int j = skutecnyStart; j <= end; j++) {
                    if (jePrvocislo(j)) {
                        prozatimniPocet++;
                    }
                }
                synchronized (System.out) {
                    System.out.println("Interval " + skutecnyStart + " - " + end + ": "+ prozatimniPocet);
                }
                celkovyPocet.addAndGet(prozatimniPocet);
            });
            seznamVlaken.add(vlakno);
            vlakno.start();
        }
        for (Thread t : seznamVlaken) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return celkovyPocet.get();
    }
}