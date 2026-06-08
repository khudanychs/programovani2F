package lekce205;
public class DU_BubbleSort {
        public static void main(String[] args) {
            double[] cisla = {8.6, 6.7, 9.1, 6.9, 1.1, 3.2};
            System.out.print("Puvodni pole: ");
            vypisPole(cisla);

            double[] proBubble = cisla.clone();
            bubbleSort(proBubble, true);
            System.out.print("Serazeno (Bubble sort, vzestupne): ");
            vypisPole(proBubble);

            double[] proSelection = cisla.clone();
            selectionSort(proSelection, false);
            System.out.print("Serazeno (Selection sort, sestupne): ");
            vypisPole(proSelection);

            double[] proInsert = cisla.clone();
            insertSort(proInsert, true);
            System.out.print("Serazeno (Insert sort, vzestupne): ");
            vypisPole(proInsert);
        }
        public static void vypisPole(double[] pole) {
            for (double c : pole) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        //bubble
        public static double[] bubbleSort(double[] data, boolean ascending) {
            int delka = data.length;
            for (int i = 0; i < delka - 1; i++) {
                for (int j = 0; j < delka - i - 1; j++) {
                    boolean prohodit = ascending ? (data[j] > data[j + 1]) : (data[j] < data[j + 1]);
                    if (prohodit) {
                        double zaloha = data[j];
                        data[j] = data[j + 1];
                        data[j + 1] = zaloha;
                    }
                }
            }
            return data;
        }
//selection
        public static double[] selectionSort(double[] data, boolean vzestupne) {
            int delka = data.length;
            for (int i = 0; i < delka - 1; i++) {
                int indexExtremu = i;
                for (int j = i + 1; j < delka; j++) {
                    boolean jeLepsi = vzestupne ? (data[j] < data[indexExtremu]) : (data[j] > data[indexExtremu]);
                    if (jeLepsi) {
                        indexExtremu = j;
                    }
                }
                double zaloha = data[indexExtremu];
                data[indexExtremu] = data[i];
                data[i] = zaloha;
            }
            return data;
        }
//insert
        public static double[] insertSort(double[] data, boolean vzestupne) {
            int delka = data.length;
            for (int i = 1; i < delka; i++) {
                double key = data[i];
                int j = i - 1;
                while (j >= 0 && (vzestupne ? data[j] > key : data[j] < key)) {
                    data[j + 1] = data[j];
                    j--;
                }
                data[j + 1] = key;
            }
            return data;
        }
    }