import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PacoteRegisto {
    public Tupulo destino;
    public Tupulo origem;
    public List<String> ficheiros;
    public int pingPort;

    public PacoteRegisto(Tupulo destino,Tupulo origem,List<String> ficheiros,int pingPort) {
        this.destino = destino;
        this.origem = origem;
        this.ficheiros = ficheiros;
        this.pingPort = pingPort;
    }

    public static byte[] compactar(PacoteRegisto pr) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);
        Tupulo.compactar(pr.origem,d);
        Tupulo.compactar(pr.destino,d);
        d.writeInt(pr.pingPort);
        d.writeInt(pr.ficheiros.size());
        for (String s : pr.ficheiros) {
            d.writeInt(s.length());
            d.write(s.getBytes());
        }
        return out.toByteArray();
    }


    public static PacoteRegisto descompactar(byte[] pacote) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(pacote);
        DataInputStream d = new DataInputStream(in);
        Tupulo or = Tupulo.descompactar(d);
        Tupulo dest = Tupulo.descompactar(d);
        int ping = d.readInt();
        List<String> fic = new ArrayList<>();
        int size = d.readInt();
        for (int i = 0; i<size; i++) {
            int tam = d.readInt();
            byte[] str = new byte[tam];
            d.readFully(str);
            String n = new String(str);
            fic.add(n);
        }
        return new PacoteRegisto(dest,or,fic,ping);
    }

    @Override
    public String toString() {
        return "PacoteRegisto{" +
                "destino=" + destino +
                ", origem=" + origem +
                ", ficheiros=" + ficheiros +
                ", pingPort=" + pingPort +
                '}';
    }
}

