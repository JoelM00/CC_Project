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
            DatagramSocket s = new DatagramSocket(portaParaComunicacaoGW_FFS);

            byte[] pacote = new byte[1024];

            PacotePedido pp = new PacotePedido(dadosDoFFS,origem,ficheiroPedido);
            pacote = PacotePedido.compactar(pp);

            DatagramPacket pedidoDeFicheiro = new DatagramPacket(pacote,pacote.length,dadosDoFFS.endereco,dadosDoFFS.porta); //crio o pacote UDP

            s.send(pedidoDeFicheiro);

            while (true) {
                byte[] pacote2 = new byte[1024];

                DatagramPacket pacoteDeChunk= new DatagramPacket(pacote2, pacote2.length);

                s.receive(pacoteDeChunk);

                PacoteChunk pc = PacoteChunk.descompactar(pacote2);

                this.pacoteComFicheiroPedido.add(pc);

                if (pc.ultimo) break;
            }

            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GatewayWorkerFFS error!");
        }
    }
}
