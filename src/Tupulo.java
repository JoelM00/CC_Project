import java.io.*;
import java.net.InetAddress;

public class Tupulo {
    InetAddress endereco;
    int porta;

    public Tupulo(InetAddress endereco, int porta) {
        this.endereco = endereco;
        this.porta = porta;
    }

    public static void compactar(Tupulo t,DataOutputStream d) throws IOException {
        d.writeInt(t.endereco.getAddress().length);
        d.write(t.endereco.getAddress());
        d.writeInt(t.porta);
    }

    public static Tupulo descompactar(DataInputStream d) throws IOException {
        int length = d.readInt();
        byte[] e = new byte[length];
        d.readFully(e);
        InetAddress end = InetAddress.getByAddress(e);
        int pt = d.readInt();
        return new Tupulo(end,pt);
    }

    @Override
    public String toString() {
        return "Tupulo{" +
                "endereco=" + endereco +
                ", porta=" + porta +
                '}';
    }
}
