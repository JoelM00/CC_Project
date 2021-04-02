import java.io.*;
import java.util.Arrays;

public class PacoteChunk {
    byte[] dados;
    int inicio;
    int fim;
    boolean ultimo;
    Tupulo destino;

    public PacoteChunk(byte[] dados, int inicio, int fim, boolean ultimo, Tupulo destino) {
        this.dados = dados;
        this.inicio = inicio;
        this.fim = fim;
        this.destino = destino;
        this.ultimo = ultimo;
    }

    public static byte[] compactar(PacoteChunk pc) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);
        d.writeInt(pc.dados.length);
        d.write(pc.dados);
        d.writeInt(pc.inicio);
        d.writeInt(pc.fim);
        d.writeBoolean(pc.ultimo);
        Tupulo.compactar(pc.destino,d);
        return out.toByteArray();
    }

    public static PacoteChunk descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);
        int size = d.readInt();
        byte[] dd = new byte[size];
        d.readFully(dd);
        int inicio = d.readInt();
        int fim = d.readInt();
        boolean ultimo = d.readBoolean();
        Tupulo dest = Tupulo.descompactar(d);
        return new PacoteChunk(dd,inicio,fim,ultimo,dest);
    }

    @Override
    public String toString() {
        return "PacoteChunk{" +
                "dados=" + Arrays.toString(dados) +
                ", inicio=" + inicio +
                ", fim=" + fim +
                ", destino=" + destino +
                '}';
    }
}
