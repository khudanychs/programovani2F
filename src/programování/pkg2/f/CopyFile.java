package programování.pkg2.f;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyFile {

    public static void main(String[] args) {
        try {
            System.out.println(copy("source.txt", "destination.txt"));
        } catch (IOException ex) {
            System.err.println("Copy file failed: " + ex.getMessage());
        }
    }

    private static long copy(String sourceName, String destinationName)
            throws IOException {
        byte[] buffer = new byte[4096];
        long total = 0L;
        int read;
        // -------------- zde odstrihnout (zacatek) -----------------
//        InputStream source = new FileInputStream(sourceName);
//        OutputStream destination = new FileOutputStream(destinationName);
//        while ((read = source.read(buffer)) > 0) {
//            destination.write(buffer, 0, read);
//            total += read;
//        }
//        destination.close();
//        source.close();
        try (InputStream source = new FileInputStream(sourceName);
             OutputStream destination = new FileOutputStream(destinationName)) {
            while ((read = source.read(buffer)) > 0) {
                destination.write(buffer, 0, read);
                total += read;
            }
        }

        // -------------- zde odstrihnout (konec) -----------------
        return total;
    }
}