import java.io.*;

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
        Tupulo.compactar(pc.destino,d);
        d.writeInt(pc.dados.length);
        d.write(pc.dados);
        d.writeLong(pc.inicio);
        d.writeLong(pc.fim);
        return out.toByteArray();
    }

    public static PacoteChunk descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);
        Tupulo dest = Tupulo.descompactar(d);
        byte[] dd = new byte[d.readInt()];
        d.readFully(dd);
        long inicio = d.readLong();
        long fim = d.readLong();

        return new PacoteChunk(dd,inicio,fim,dest);
    }

}
