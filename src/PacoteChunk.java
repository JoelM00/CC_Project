import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public class PacoteChunk {
    byte[] dados;
    int inicio;
    int fim;
    boolean ultimo;
    Tupulo destino;
    Tupulo origem;

    public PacoteChunk() {}

    public PacoteChunk(byte[] dados, int inicio, int fim, boolean ultimo, Tupulo destino, Tupulo origem) {
        this.dados = dados;
        this.inicio = inicio;
        this.fim = fim;
        this.destino = destino;
        this.origem = origem;
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
        Tupulo.compactar(pc.origem,d);
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
        Tupulo ori = Tupulo.descompactar(d);
        return new PacoteChunk(dd,inicio,fim,ultimo,dest,ori);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacoteChunk that = (PacoteChunk) o;
        return inicio == that.inicio && fim == that.fim && ultimo == that.ultimo && Arrays.equals(dados, that.dados) && destino.equals(that.destino) && origem.equals(that.origem);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(inicio, fim, ultimo, destino, origem);
        result = 31 * result + Arrays.hashCode(dados);
        return result;
    }

    @Override
    public String toString() {
        return "PacoteChunk{" +
                "dados=" + Arrays.toString(dados) +
                ", inicio=" + inicio +
                ", fim=" + fim +
                ", ultimo=" + ultimo +
                ", destino=" + destino +
                ", origem=" + origem +
                '}';
    }
}
