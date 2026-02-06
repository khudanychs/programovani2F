package programování.pkg2.f;
public class factorial {
    public static void main(String[] args) {
        System.out.println(findMaxFactorial());
        System.out.println(Speedmeter.test(10000, () -> findMaxFactorial()));
    }
    public static double factorial(int n){
        if (n <= 1){
            return 1;
        }else{
            return n * factorial(n-1);
        }
    }
    public static int findMaxFactorial() {
        double f = 1;
        for (int n = 2;; n++) {
            f*= n;
            if(f == Double.POSITIVE_INFINITY) {
                return n;
            }
            
        }
    }
}
