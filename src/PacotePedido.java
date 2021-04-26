import java.io.*;

public class PacotePedido {
    Tupulo destino;
    Tupulo origem;
    String ficheiroPedido;
    boolean metaDados;
    boolean ping;
    int from;
    int to;

    public PacotePedido(Tupulo destino, Tupulo origem, String ficheiroPedido, boolean metaDados,boolean ping, int from, int to) {
        this.destino = destino;
        this.origem = origem;
        this.ficheiroPedido = ficheiroPedido;
        this.metaDados = metaDados;
        this.from = from;
        this.to = to;
        this.ping = ping;
    }

    public static byte[] compactar(PacotePedido pp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);
        Tupulo.compactar(pp.origem,d);
        Tupulo.compactar(pp.destino,d);
        d.writeInt(pp.ficheiroPedido.length());
        d.write(pp.ficheiroPedido.getBytes());
        d.writeBoolean(pp.metaDados);
        d.writeBoolean(pp.ping);
        d.writeInt(pp.from);
        d.writeInt(pp.to);
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
        boolean md = d.readBoolean();
        boolean pin = d.readBoolean();
        int f = d.readInt();
        int t = d.readInt();
        return new PacotePedido(dest,or,fic,md,pin,f,t);
    }

    @Override
    public String toString() {
        return "PacotePedido{" +
                "destino=" + destino +
                ", origem=" + origem +
                ", ficheiroPedido='" + ficheiroPedido + '\'' +
                ", metaDados=" + metaDados +
                ", ping=" + ping +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
