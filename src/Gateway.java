import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gateway {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket ss = new ServerSocket(5000); //porta de servico para os clientes
        int portaParaRegistoNoGW = 6000;
        int portaParaComunicacaoGW_FFS  = 7000;

        Map<Tupulo, List<String>> registosFFS = new HashMap<>();

        //Ativa um thread que vai escutar costantemente a porta no qual os novos FFS se devem ligar para se registar
        new Thread(new GatewayWorkerRegister(portaParaRegistoNoGW,registosFFS)).start();

        while (true) {
            Thread.sleep(10000);
            for (Map.Entry<Tupulo,List<String>> a : registosFFS.entrySet()) {
                System.out.println(a.getKey().toString());
                System.out.println(a.getValue().toString());
            }
        }

        /*
        //Recebe os pedidos dos clientes
        while (true) {
            Socket s = ss.accept();
            new Thread(new HttpWorker(s,portaParaComunicacaoGW_FFS,registosFFS)).start();
        }*/
    }
}
