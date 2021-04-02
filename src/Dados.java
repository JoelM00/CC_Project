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

    public static byte[] readCampo(byte[] dados, int from, int to) {
        int tamanhoPacote = to - from;
        byte[] buffer = new byte[tamanhoPacote];

        for (int i = from; i < to; i++) {
            buffer[i] = dados[i];
        }
        return buffer;
    }
}
