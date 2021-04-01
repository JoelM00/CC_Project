import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayWorker implements Runnable {
    private Map<Integer, List<String>> registosFFS = new HashMap<>();
    private int portaDoGW;

    public GatewayWorker(int portaDoGW) {
        this.portaDoGW = portaDoGW;
    }

    @Override
    public void run() {
        try {
            DatagramSocket s = new DatagramSocket(this.portaDoGW);

            byte[] pacote = new byte[1024];

            while (true) {
                DatagramPacket pacoteDoFFS = new DatagramPacket(pacote, pacote.length);

                s.receive(pacoteDoFFS);

                System.out.println(new String(pacote));

                /*Dados d = Dados.descompactar(pacoteDoFFS);

                if (d.pedidoDeRegisto) {
                    //adicionar ao map
                } else {
                    //chunks de algum ficheiro
                }*/
            }
        } catch (Exception e) {
            System.out.println("GatewayWorker error!");
        }
    }
}
