import java.net.InetAddress;
import java.util.List;

public class PacoteRegisto {
    Tupulo dados;
    List<String> ficheiros;

    public PacoteRegisto(Tupulo dados, List<String> ficheiros) {
        this.dados = dados;
        this.ficheiros = ficheiros;
    }

    public static byte[] compactar() {
        byte[] bytes = new byte[1024];
        //Converter o tupulo e os ficheiros num array de bytes.
        //Antes de converter a String (nome) de cada ficheiro, escrever o seu tamanho
        return bytes;
    }

    public static PacoteRegisto descompactar(byte[] pacote) {
        PacoteRegisto p;
        //Converter os bytes num tupulo e lista de ficheiros
        return null;
    }

}
