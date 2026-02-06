package programování.pkg2.f;
public class Vypis {
    public static void main(String[] args) {
        // délku běhu programu (v sekundách) lze zadat parametrem
        int dobaBehusekundy = 1000; // výchozí 1 sekunda
        if (args.length > 0) {
            try {
                dobaBehusekundy = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Používám výchozí dobu: 1 sekunda");
            }
        }

        long start = System.currentTimeMillis();
        long konec = start + dobaBehusekundy * 1000L;

        long cislo = 1;
        while (System.currentTimeMillis() < konec) {
            System.out.println(cislo);
            cislo++;
        }

        long celkem = cislo - 1;
        double frekvence = (double) celkem / dobaBehusekundy;

        System.out.println("Za " + dobaBehusekundy + " s bylo vypsáno " 
                           + celkem + " čísel.");
        System.out.println("Frekvence: " + frekvence + " čísel/sekundu.");
    } 
}
