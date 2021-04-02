import java.io.*;
import java.nio.charset.StandardCharsets;

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
        d.write(pp.ficheiroPedido.getBytes());

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
        return new PacotePedido(dest,or,fic);
    }

    @Override
    public String toString() {
        return "PacotePedido{" +
                "destino=" + destino +
                ", origem=" + origem +
                ", ficheiroPedido='" + ficheiroPedido + '\'' +
                '}';
    }
}
