package Test17_4_2026;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
//1. Napište tělo statické metody sortStringsIgnoreCase(List<String> strings), která seřadí předanou kolekci řetězců abecedně (ASCII) nerozlišující při tom velká a malá písmena.
//
//Metoda String.compareToIgnoreCase(String) porovná dva řetězce přesně tímto způsobem.
//
//Pro seřazení kolekce použijte statickou metodu Collections.sort(List, Comparator), ta má na rozdíl od své přetížené varianty s jedním argumentem Comparator, což je objekt implementující toto rozhraní.
//
//Zvolte takovou implementaci, která umožní veškerý kód umístit dovnitř vytvářené metody.
//
//Jedinou metodou rozhraní je metoda compare(String o1, String o2), která má vracet stejnou hodnotu, jakou vrací metoda String.compareToIgnoreCase(String), aby byla kolekce seřazena podle zadaného kritéria.
//
//Do vývojového prostředí zkopírujte vzorový kód níže. Výsledek pak vložte do editačního pole pod vzorem. Vložte pouze tělo metody, jak je naznačeno ve vzorovém příkladu.
//
//Tipy:
//anonymní vnitřní třída
//Comparator<String>
//[Abrahám, Adam, ahoj, Alois, Asterix]
class Sorter {

    public static void sortStringsIgnoreCase(List<String> strings) {
        strings.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
    }

    public static void main(String[] args) {
        List<String> strings = Arrays.asList(new String[]{"ahoj", "Alois", "Adam", "Abrahám", "Asterix"});
        sortStringsIgnoreCase(strings);
        System.out.println(strings);
    }
}

