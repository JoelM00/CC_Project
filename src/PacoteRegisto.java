
import java.io.*;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PacoteRegisto {
    public Tupulo destino;
    public Tupulo origem;
    public List<String> ficheiros;

    public PacoteRegisto(Tupulo destino,Tupulo origem,List<String> ficheiros) {
        this.destino = destino;
        this.origem = origem;
        this.ficheiros = ficheiros;
    }

    public static byte[] compactar(PacoteRegisto pr) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);
        Tupulo.compactar(pr.origem,d);
        Tupulo.compactar(pr.destino,d);
        d.writeInt(pr.ficheiros.size());
        for (String s : pr.ficheiros) {
            d.writeInt(s.length());
            d.writeChars(s);
        }
        return out.toByteArray();
    }


    public static PacoteRegisto descompactar(byte[] pacote) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(pacote);
        DataInputStream d = new DataInputStream(in);
        Tupulo or = Tupulo.descompactar(d);
        Tupulo dest = Tupulo.descompactar(d);
        List<String> fic = new ArrayList<>();
        int size = d.readInt();
        for (int i = 0; i<size; i++) {
            byte[] str = new byte[d.readInt()];
            d.readFully(str);
            fic.add(new String(str));
        }
        return new PacoteRegisto(or,dest,fic);
    }

}

