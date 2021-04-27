import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class GatewayWorkerFFS {
    String ficheiroPedido;
    Tupulo dadosDoFFS;
    HttpWorker.PacoteFicheiro pacoteFicheiro;
    RegistosFFS registosFFS;
    int from;
    int to;

    public GatewayWorkerFFS(Tupulo dadosDoFFS, RegistosFFS registosFFS, String ficheiroPedido, HttpWorker.PacoteFicheiro pacoteFicheiro, int from, int to) {
        this.dadosDoFFS = dadosDoFFS;
        this.ficheiroPedido = ficheiroPedido;
        this.pacoteFicheiro = pacoteFicheiro;
        this.registosFFS = registosFFS;
        this.from = from;
        this.to = to;
    }

    public void run() {
        try {
            DatagramSocket s = new DatagramSocket(0);
            Tupulo origem = new Tupulo(InetAddress.getLocalHost(),s.getLocalPort());

            byte[] pacote = new byte[1024];

            PacotePedido pp = new PacotePedido(dadosDoFFS,origem,ficheiroPedido,false,false,from,to);
            pacote = PacotePedido.compactar(pp);
            DatagramPacket pedidoDeFicheiro = new DatagramPacket(pacote,pacote.length,dadosDoFFS.endereco,dadosDoFFS.porta);

            int codigo = new ConexaoFiavel(s,pedidoDeFicheiro).envia();
            if (codigo == 0) {
                while (true) {

                    PacoteChunk pc = new ConexaoFiavel(s).recebeChunk(origem);

                    if (pc == null) {
                        System.out.println(" -> FFS deixou de enviar chunks!");
                        this.pacoteFicheiro.pacotes.clear();
                        break;
                    }

                    this.pacoteFicheiro.l.lock();
                    this.pacoteFicheiro.pacotes.add(pc);
                    this.pacoteFicheiro.l.unlock();

                    new ConexaoFiavel(s).enviaACK(pc.origem);

                    if (pc.ultimo) break;
                }

                s.close();

            } else {
                    this.registosFFS.remove(dadosDoFFS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" >>> GatewayWorkerFFS error!");
        }
    }
}

