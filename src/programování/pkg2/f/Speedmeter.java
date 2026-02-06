package programování.pkg2.f;
public class Speedmeter {
    public static void main(String[] args) {
        System.out.println(
            test(1, () -> {
                double t = 10.0;
                double s = 1.0 / 2.0 * 9.81 * Math.pow(t, 2.0);
        })
        );
    }

    //------------------------------------------------//  

    public static long test(int count, Runnable what) {
        long started = System.nanoTime();
        for (int i = 0; i < count; i++) {
            what.run();
        }
        long finished = System.nanoTime();
        return (finished - started) / count;
    }
}