import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Gateway {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5000);           //porta de servico para os clientes
        int portaParaRegistoNoGW = 6000;
        RegistosFFS registosFFS = new RegistosFFS();

        //Ativa um thread que vai escutar costantemente a porta no qual os novos FFS se devem ligar para se registar
        new Thread(new GatewayWorkerRegister(portaParaRegistoNoGW,registosFFS)).start();

        //Recebe os pedidos dos clientes
        while (true) {
            Socket s = ss.accept();
            System.out.println("$$ Recebi pedido de cliente! $$");
            new Thread(new HttpWorker(s,registosFFS)).start();
        }
    }
}
