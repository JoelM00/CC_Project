import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Gateway {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(50000); //porta de servico para os clientes
        int portaParaRegistoNoGW = 6000;
        RegistosFFS registosFFS = new RegistosFFS();
        InetAddress endereco = InetAddress.getByName(args[0]);

        //Ativa um thread que vai escutar costantemente a porta no qual os novos FFS se devem ligar para se registar
        new Thread(new GatewayWorkerRegister(endereco,portaParaRegistoNoGW,registosFFS)).start();

        //Recebe os pedidos dos clientes
        while (true) {
            Socket s = ss.accept();
            System.out.println("$$ Recebi pedido de cliente! $$");
            new Thread(new HttpWorker(endereco,s,registosFFS)).start();
        }
    }
}
