import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class GatewayWorkerFFS implements Runnable {
    private String ficheiroPedido;
    private Tupulo dadosDoFFS;
    private List<PacoteChunk> pacoteComFicheiroPedido;
    private int portaParaComunicacaoGW_FFS;

    public GatewayWorkerFFS(Tupulo dadosDoFFS, String ficheiroPedido, List<PacoteChunk> pacoteComFicheiroPedido, int portaParaComunicacaoGW_FFS) {
        this.dadosDoFFS = dadosDoFFS;
        this.ficheiroPedido = ficheiroPedido;
        this.pacoteComFicheiroPedido = pacoteComFicheiroPedido;
        this.portaParaComunicacaoGW_FFS = portaParaComunicacaoGW_FFS;
    }

    @Override
    public void run() {
        try {
            Tupulo origem = new Tupulo(InetAddress.getLocalHost(),portaParaComunicacaoGW_FFS);
            //Se houver problemas com sockets, o problema pode estar aqui
            DatagramSocket s = new DatagramSocket(portaParaComunicacaoGW_FFS);

            byte[] pacote = new byte[1024];

            PacotePedido pp = new PacotePedido(dadosDoFFS,origem,ficheiroPedido);
            pacote = PacotePedido.compactar(pp);

            DatagramPacket pedidoDeFicheiro = new DatagramPacket(pacote, pacote.length);

            s.send(pedidoDeFicheiro);

            while (true) {
                DatagramPacket chunk = new DatagramPacket(pacote, pacote.length);

                s.receive(chunk);

                PacoteChunk pc = PacoteChunk.descompactar(chunk.getData());

                this.pacoteComFicheiroPedido.add(pc);

            }

        } catch (Exception e) {
            System.out.println("GatewayWorkerFFS error!");
        }
    }
}
