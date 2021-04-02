import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FastFileServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket s = new DatagramSocket(9998);
        Map<String,byte[]> ficheiros = new HashMap<>();
        Tupulo gateway = new Tupulo(InetAddress.getLocalHost(),6000);
        Tupulo origem = new Tupulo(InetAddress.getLocalHost(),9998);//para ja ambos tem o mesmo endereco

        byte[] pacote = new byte[1024];

        ficheiros.put("xau.txt","algum lixo".getBytes()); //adicionei um ficheiro a servir

        PacoteRegisto pr = new PacoteRegisto(gateway,origem,new ArrayList<>(ficheiros.keySet())); //crio um pacote de registo

        pacote = PacoteRegisto.compactar(pr);
        DatagramPacket pedidoDeRegistoNoGW = new DatagramPacket(pacote,pacote.length,gateway.endereco,gateway.porta); //crio o pacote UDP

        s.send(pedidoDeRegistoNoGW); // envio ao gateway

        //Esperar ACK (caso se perca)
        //...

        //Espera por pedidos de ficheiros.
        while (true) {
            DatagramPacket pedidoDeFicheiro = new DatagramPacket(pacote, pacote.length);

            s.receive(pedidoDeFicheiro);

            PacotePedido pp = PacotePedido.descompactar(pedidoDeFicheiro.getData());

            new Thread(new FastFileWorker(s,pp.ficheiroPedido,pp.origem,pp.destino)); //cria a thread que vai enviar o ficheiro ao gateway
        }
    }
}
