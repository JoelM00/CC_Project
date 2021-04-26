import java.io.*;

public class PacoteACK {
    int codigo;

    public PacoteACK(int codigo) {
        this.codigo = codigo;
    }

    public static byte[] compactar(PacoteACK pa) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);

        d.writeInt(pa.codigo);

        return out.toByteArray();
    }

    public static PacoteACK descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);

        int num = d.readInt();

        return new PacoteACK(num);
    }
}
