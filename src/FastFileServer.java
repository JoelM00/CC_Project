import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FastFileServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket s = new DatagramSocket(0);
        DatagramSocket p = new DatagramSocket(0);
        Map<String,Integer> ficheiros = new HashMap<>();
        Tupulo gateway = new Tupulo(InetAddress.getLocalHost(),6000);
        Tupulo origem = new Tupulo(InetAddress.getLocalHost(),s.getLocalPort());
        int pingPort = p.getLocalPort();

        byte[] pacote = new byte[1024];
        byte[] pacote1 = new byte[1024];
        byte[] pacote2 = new byte[1024];

        ficheiros.put("ola.txt",89673);
        ficheiros.put("xau.txt",2026983);
        ficheiros.put("hello.txt",644719);
        ficheiros.put("json.txt",79150);
        ficheiros.put("lixo.txt",112);


        PacoteRegisto pr = new PacoteRegisto(gateway,origem,new ArrayList<>(ficheiros.keySet()),pingPort);
        pacote = PacoteRegisto.compactar(pr);
        DatagramPacket pedidoDeRegistoNoGW = new DatagramPacket(pacote,pacote.length,gateway.endereco,gateway.porta);

        int codigo = new ConexaoFiavel(s,pedidoDeRegistoNoGW).envia();
        if (codigo == 1) {
            System.out.println("@ Gateway indisponivel!");
            return;
        }

        while (true) {
            DatagramPacket pedidoDeFicheiro = new DatagramPacket(pacote1, pacote1.length);
            s.receive(pedidoDeFicheiro);
            PacotePedido pp = PacotePedido.descompactar(pacote1);

            new ConexaoFiavel(s).enviaACK(pp.origem);

            if (pp.metaDados) {
                System.out.println(" -> Recebi o pedido de metadados!");

                //Alerta! Aqui os campos sao usados de forma diferente. O campo destino aqui, tem o outro socket da origem que esta a espera da resposta.
                PacoteResposta resp = new PacoteResposta(ficheiros.get(pp.ficheiroPedido));
                pacote2 = PacoteResposta.compactar(resp);
                DatagramPacket respMeta = new DatagramPacket(pacote2, pacote2.length, pp.destino.endereco, pp.destino.porta);

                s.send(respMeta);

            } else if (pp.ping) {
                //System.out.println(" -> Recebi o pedido de ping!");

            } else {
                System.out.println(" -> Recebi o pedido de ficheiro!");
                new Thread(new FastFileWorker(pp.ficheiroPedido, pp.origem, pp.from, pp.to)).start();
            }
        }
    }
}
