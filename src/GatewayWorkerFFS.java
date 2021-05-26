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
    InetAddress endereco;

    public GatewayWorkerFFS(InetAddress endereco, Tupulo dadosDoFFS, RegistosFFS registosFFS, String ficheiroPedido, HttpWorker.PacoteFicheiro pacoteFicheiro, int from, int to) {
        this.dadosDoFFS = dadosDoFFS;
        this.ficheiroPedido = ficheiroPedido;
        this.pacoteFicheiro = pacoteFicheiro;
        this.registosFFS = registosFFS;
        this.from = from;
        this.to = to;
        this.endereco = endereco;
    }

    public void run() {
        try {
            DatagramSocket s = new DatagramSocket(0);
            Tupulo origem = new Tupulo(endereco,s.getLocalPort());

            byte[] pacote = new byte[1024];

            PacotePedido pp = new PacotePedido(dadosDoFFS,origem,ficheiroPedido,false,false,from,to);
            pacote = PacotePedido.compactar(pp);
            DatagramPacket pedidoDeFicheiro = new DatagramPacket(pacote,pacote.length,dadosDoFFS.endereco,dadosDoFFS.porta);

            int codigo = new ConexaoFiavel(s,pedidoDeFicheiro).envia();
            if (codigo == 0) {

                PacoteChunk anterior = new PacoteChunk();

                while (true) {

                    PacoteChunk pc = new ConexaoFiavel(s).recebeChunk(origem);

                    if (pc == null) {
                        System.out.println(" -> FFS deixou de enviar chunks!");
                        this.pacoteFicheiro.pacotes.clear();
                        break;
                    }

                    if (!pc.equals(anterior)) {
                        this.pacoteFicheiro.l.lock();
                        this.pacoteFicheiro.pacotes.add(pc);
                        this.pacoteFicheiro.l.unlock();
                    }

                    new ConexaoFiavel(s).enviaACK(pc.origem);

                    anterior = pc;

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

