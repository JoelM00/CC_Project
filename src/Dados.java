import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class Dados {

    public static byte[] read(String file, long from, long to) throws Exception {
        System.out.println("\n\nLendo de "+from +" ate "+to);

        FileInputStream fin = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(fin);

        int tamanhoPacote = (int) (to-from);

        byte[] buffer = new byte[tamanhoPacote];
        try {
            bin.skip(from);
            bin.read(buffer,0,tamanhoPacote);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bin.close();
        }
        return buffer;
    }
}
