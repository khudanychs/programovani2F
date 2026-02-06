package programování.pkg2.f;
public class ErrorExample {
    public static void main(String[] args) {
        Error er = new OutOfMemoryError();
        throw er;
    }
}
