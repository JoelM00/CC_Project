import java.io.*;

public class PacoteResposta {
    int tamanhoFicheiro;

    public PacoteResposta(int tamanhoFicheiro) {
        this.tamanhoFicheiro = tamanhoFicheiro;
    }

    public static byte[] compactar(PacoteResposta pr) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);

        d.writeInt(pr.tamanhoFicheiro);

        return out.toByteArray();
    }

    public static PacoteResposta descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);

        int num = d.readInt();

        return new PacoteResposta(num);
    }
}
