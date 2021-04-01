import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Gateway {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5000);

        //Ativa o lado que interage com os FFS
        new Thread(new GatewayWorker(6000)).start();

        //Recebe os pedidos dos clientes
        /*while (true) {
            Socket s = ss.accept();
            new Thread(new HttpWorker(s)).start();
        }*/
    }
}
