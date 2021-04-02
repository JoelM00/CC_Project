import java.io.*;

public class PacotePedido {
    Tupulo destino;
    Tupulo origem;
    String ficheiroPedido;

    public PacotePedido(Tupulo destino, Tupulo origem, String ficheiroPedido) {
        this.destino = destino;
        this.origem = origem;
        this.ficheiroPedido = ficheiroPedido;
    }

    public static byte[] compactar(PacotePedido pp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);
        Tupulo.compactar(pp.origem,d);
        Tupulo.compactar(pp.destino,d);
        d.writeInt(pp.ficheiroPedido.length());
        d.writeChars(pp.ficheiroPedido);

        return out.toByteArray();
    }

    public static PacotePedido descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);
        Tupulo or = Tupulo.descompactar(d);
        Tupulo dest = Tupulo.descompactar(d);
        byte[] str = new byte[d.readInt()];
        d.readFully(str);
        String fic = new String(str);
        return new PacotePedido(or,dest,fic);
    }
}
