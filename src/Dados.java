import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class Dados {

    public static byte[] read(String file, long from, long to) throws Exception {

        FileInputStream fin = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(fin);

        int tamanhoPacote = (int) (to-from);

        byte[] buffer = new byte[tamanhoPacote];

        int lidos = 0;
        try {
            bin.skip(from);
            lidos = bin.read(buffer,0,tamanhoPacote);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bin.close();
        }

        byte[] bufferUtil = new byte[lidos];
        int pos = 0;
        for (int i = 0; i<lidos; i++) {
            bufferUtil[pos++] = buffer[i];
        }

        return bufferUtil;
    }
}

