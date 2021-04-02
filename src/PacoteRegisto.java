import java.util.List;

public class PacoteRegisto {
    Tupulo destino;
    Tupulo origem;
    List<String> ficheiros;

    public PacoteRegisto(Tupulo destino,Tupulo origem,List<String> ficheiros) {
        this.destino = destino;
        this.origem = origem;
        this.ficheiros = ficheiros;
    }

    public static byte[] compactar(PacoteRegisto p) {
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
