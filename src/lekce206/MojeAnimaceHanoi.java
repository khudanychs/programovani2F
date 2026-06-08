package lekce206;

import cz.gyarab.util.teaser.Hanoi;
import cz.gyarab.util.teaser.HanoiAnimator;
import cz.gyarab.util.teaser.HanoiSolver;
import javafx.util.Duration; // Nezapomeň na tento import!

public class MojeAnimaceHanoi {

    public static void main(String[] args) {
        // --- TADY NASTAVÍŠ RYCHLOST ---
        // Například 500 milisekund (půl sekundy) místo původních 2 sekund
        HanoiAnimator.setDuration(Duration.millis(16.6));

        int pocetDisku = 10;
        HanoiTowers mojeLogika = new HanoiTowers();
        Hanoi animator = HanoiAnimator.createAnimator(mojeLogika);
        HanoiSolver automatickyResitel = new HanoiSolver(animator);

        automatickyResitel.solve(pocetDisku);
    }
}