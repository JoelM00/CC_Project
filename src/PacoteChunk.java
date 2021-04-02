import java.io.*;
import java.util.Arrays;

public class PacoteChunk {
    byte[] dados;
    long inicio;
    long fim;
    Tupulo destino;

    public PacoteChunk(byte[] dados, long inicio, long fim, Tupulo destino) {
        this.dados = dados;
        this.inicio = inicio;
        this.fim = fim;
        this.destino = destino;
    }

    public static byte[] compactar(PacoteChunk pc) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);
        d.writeInt(pc.dados.length);
        d.write(pc.dados);
        d.writeLong(pc.inicio);
        d.writeLong(pc.fim);
        Tupulo.compactar(pc.destino,d);
        return out.toByteArray();
    }

    public static PacoteChunk descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);
        int size = d.readInt();
        byte[] dd = new byte[size];
        d.readFully(dd);
        long inicio = d.readLong();
        long fim = d.readLong();
        Tupulo dest = Tupulo.descompactar(d);
        return new PacoteChunk(dd,inicio,fim,dest);
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
