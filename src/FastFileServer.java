import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class FastFileServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket s = new DatagramSocket(10000);
        InetAddress enderecoDoGW = InetAddress.getLocalHost();
        Map<String,byte[]> ficheiros = new HashMap<>();

        byte[] pacote = new byte[1024];

        DatagramPacket pedidoDeRegistoNoGW = new DatagramPacket(pacote, pacote.length);


        new Thread(new FastFileWorker("ola.txt", 0, 64,s,6000,enderecoDoGW)).start();
        /*
        while (true) {
            DatagramPacket pedidoDoGW = new DatagramPacket(pacote, pacote.length);

            s.receive(pedidoDoGW);

            Dados d = Dados.descompactar(pedidoDoGW);

            new Thread(new FastFileWorker(d.fileName, d.from, d.to, d.portaDoFFS, d.portaDoGW,enderecoDoGW));
        }*/
    }
}
