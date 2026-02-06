package programování.pkg2.f;

public class DateTime {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2; i++) {
            String s = "abc";
            s+= "def";
            System.out.println(s);
            
        }
        long end = System.nanoTime();
        System.out.println(end - start);
    }
}
