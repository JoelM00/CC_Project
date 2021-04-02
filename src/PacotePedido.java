public class PacotePedido {
    Tupulo destino;
    Tupulo origem;
    String ficheiroPedido;

    public PacotePedido(Tupulo destino, Tupulo origem, String ficheiroPedido) {
        this.destino = destino;
        this.origem = origem;
        this.ficheiroPedido = ficheiroPedido;
    }

    public static byte[] compactar(PacotePedido pp) {
        byte[] bytes = new byte[1024];
        //Converter o tupulo e os ficheiros num array de bytes.
        //Antes de converter a String (nome) de cada ficheiro, escrever o seu tamanho
        return bytes;
    }

    public static PacotePedido descompactar(byte[] dados) {
        PacoteRegisto p;
        //Converter os bytes num tupulo e lista de ficheiros
        return null;
    }

}
