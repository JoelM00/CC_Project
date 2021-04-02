
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

    public static byte[] compactar(PacoteChunk pc) {
        byte[] bytes = new byte[1024];
        //Converter o tupulo e os ficheiros num array de bytes.
        //Antes de converter a String (nome) de cada ficheiro, escrever o seu tamanho
        return bytes;
    }

    public static PacoteChunk descompactar(byte[] dados) {
        PacoteRegisto p;
        //Converter os bytes num tupulo e lista de ficheiros
        return null;
    }

}
