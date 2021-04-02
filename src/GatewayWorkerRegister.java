import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Map;

public class GatewayWorkerRegister implements Runnable {
    private int portaParaRegistoNoGW;
    private Map<Tupulo, List<String>> registosFFS;

    public GatewayWorkerRegister(int portaParaRegistoNoGW, Map<Tupulo, List<String>> registosFFS) {
        this.portaParaRegistoNoGW = portaParaRegistoNoGW;
        this.registosFFS = registosFFS;
    }

    @Override
    public void run() {
        try {
            DatagramSocket s = new DatagramSocket(this.portaParaRegistoNoGW);

            byte[] pacote = new byte[1024];

            while (true) {
                DatagramPacket pacoteDeResgisto = new DatagramPacket(pacote, pacote.length);

                s.receive(pacoteDeResgisto);

                PacoteRegisto p = PacoteRegisto.descompactar(pacote);

                this.registosFFS.put(p.origem,p.ficheiros);
                System.out.println("Efetuado registo de: "+p.origem.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GatewayWorkerRegister error!");
        }
    }
}
