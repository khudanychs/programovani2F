package lekce206;

import cz.gyarab.util.teaser.Hanoi;
import java.util.Stack;

public class HanoiTowers implements Hanoi {

    // Použijeme 3 zásobníky (Stacks) pro reprezentaci 3 věží.
    // Uvnitř budou celá čísla představující velikosti disků.
    private Stack<Integer> left = new Stack<>();
    private Stack<Integer> middle = new Stack<>();
    private Stack<Integer> right = new Stack<>();

    // Zapamatujeme si celkový počet disků pro finální kontrolu v metodě done()
    private int totalDisks;

    // Pomocná metoda pro snadné vrácení správného zásobníku podle Enumu Place
    private Stack<Integer> getPeg(Place place) {
        switch (place) {
            case LEFT: return left;
            case MIDDLE: return middle;
            case RIGHT: return right;
            default: throw new IllegalArgumentException("Neznámá věž");
        }
    }

    /**
     * Řeší problém Hanojských věží rekurzivně.
     * Místo znaků (char) rovnou využíváme knihovní Enum Place.
     */
    public void solve(int n, Place source, Place dest, Place aux) {
        if (n == 1) {
            // Místo obyčejného textového výpisu zavoláme naši kontrolovanou metodu move()
            move(source, dest);
            return;
        }

        // Přesun n-1 disků ze zdroje na pomocnou věž
        solve(n - 1, source, aux, dest);

        // Přesun největšího disku na cílovou věž
        move(source, dest);

        // Přesun n-1 disků z pomocné věže na cílovou
        solve(n - 1, aux, dest, source);
    }

    public static void main(String[] args) {
        HanoiTowers game = new HanoiTowers();
        int n = 5; // Počet disků

        System.out.println("Inicializace hry...");
        game.setup(n);

        System.out.println("Začínám řešit přesuny...");
        game.solve(n, Place.LEFT, Place.RIGHT, Place.MIDDLE);

        // Závěrečná kontrola
        game.done();
    }

    @Override
    public void setup(int count) throws IllegalArgumentException {
        if (count <= 0) {
            throw new IllegalArgumentException("Počet disků musí být kladné číslo.");
        }
        this.totalDisks = count;

        // Vyprázdníme všechny věže (užitečné, pokud bychom metodu setup volali opakovaně)
        left.clear();
        middle.clear();
        right.clear();

        // Na levou věž naskládáme disky od největšího po nejmenší.
        // Největší disk dáme jako první (hodnota např. 5) a na něj menší, vrchní disk bude 1.
        for (int i = count; i >= 1; i--) {
            left.push(i);
        }
    }

    @Override
    public void move(Place from, Place to) throws IllegalStateException {
        Stack<Integer> sourcePeg = getPeg(from);
        Stack<Integer> destPeg = getPeg(to);

        // Pravidlo 1: Nelze přesouvat z prázdné věže
        if (sourcePeg.isEmpty()) {
            throw new IllegalStateException("Neplatný tah: Zdrojová věž " + from + " je prázdná.");
        }

        // Zjistíme, jaký disk chceme přesunout (pomocí peek() se jen podíváme na vršek zásobníku)
        int diskToMove = sourcePeg.peek();

        // Pravidlo 2: Nikdy nesmíme položit větší disk na menší
        if (!destPeg.isEmpty() && destPeg.peek() < diskToMove) {
            throw new IllegalStateException(
                    "Porušení pravidel! Nelze položit disk velikosti " + diskToMove +
                            " na menší disk velikosti " + destPeg.peek() + " (věž " + to + ")."
            );
        }

        // Pokud je vše v pořádku, disk reálně odebereme ze zdroje (pop) a přidáme do cíle (push)
        destPeg.push(sourcePeg.pop());

        System.out.println("Byl přesunut disk " + diskToMove + " z " + from + " na " + to);
    }

    @Override
    public void done() throws IllegalStateException {
        // Hra je úspěšně u konce tehdy, když nezbyl žádný disk vlevo ani uprostřed
        // a pravá věž obsahuje správný celkový počet disků.
        if (!left.isEmpty() || !middle.isEmpty() || right.size() != totalDisks) {
            throw new IllegalStateException("Hra není dokončena správně! Všechny disky musí být na pravé věži.");
        }
        System.out.println("Výborně, hlavolam je úspěšně vyřešen v souladu s pravidly!");
    }
}