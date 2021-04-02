import java.util.List;

public class GatewayWorkerFFS implements Runnable {
    private String ficheiroPedido;
    private Tupulo dadosDoFFS;
    private List<byte[]> pacoteComFicheiroPedido;

    public GatewayWorkerFFS(Tupulo dadosDoFFS, String ficheiroPedido, List<byte[]> pacoteComFicheiroPedido) {
        this.dadosDoFFS = dadosDoFFS;
        this.ficheiroPedido = ficheiroPedido;
        this.pacoteComFicheiroPedido = pacoteComFicheiroPedido;
    }

    @Override
    public void run() {
        try {



        } catch (Exception e) {
            System.out.println("GatewayWorkerFFS error!");
        }
    }
}
