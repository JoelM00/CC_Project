import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GatewayWorkerRegister implements Runnable {
    class Ping implements Runnable {
        RegistosFFS registosFFS;
        InetAddress endereco;

        public Ping(InetAddress endereco, RegistosFFS registosFFS) {
            this.registosFFS = registosFFS;
            this.endereco = endereco;
        }

        @Override
        public void run() {
            try {
                DatagramSocket s = new DatagramSocket(0);
                Tupulo origem = new Tupulo(endereco,s.getLocalPort());
                Map<Tupulo,Integer> regs;
                List<Tupulo> aRemover = null;

                while (true) {
                    aRemover = new ArrayList<>();
                    try {
                        registosFFS.rl.lock();
                        for (Tupulo ffs : registosFFS.registosFFS.keySet()) {

                            byte[] pacote = new byte[1024];

                            PacotePedido pp = new PacotePedido(ffs, origem, "", false, true, 0, 0);
                            pacote = PacotePedido.compactar(pp);
                            DatagramPacket pedido = new DatagramPacket(pacote, pacote.length, ffs.endereco, ffs.porta);

                            int codigo = new ConexaoFiavel(s, pedido).envia();

                            if (codigo == 1) {
                                System.out.println(" -> Ping para: " + ffs + " sem resposta!");
                                aRemover.add(ffs);
                                s = new DatagramSocket(0);
                                origem = new Tupulo(endereco, s.getLocalPort());
                            }
                        }
                    } finally {
                        registosFFS.rl.unlock();
                    }
                    for (Tupulo ffs : aRemover) {
                        registosFFS.remove(ffs);
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(" >>> Ping error!");
            }
        }
    }

    private int portaParaRegistoNoGW;
    private RegistosFFS registosFFS;
    InetAddress endereco;

    public GatewayWorkerRegister(InetAddress endereco, int portaParaRegistoNoGW, RegistosFFS registosFFS) {
        this.portaParaRegistoNoGW = portaParaRegistoNoGW;
        this.registosFFS = registosFFS;
        this.endereco = endereco;
    }

    @Override
    public void run() {
        try {
            DatagramSocket s = new DatagramSocket(this.portaParaRegistoNoGW);
            new Thread(new Ping(endereco,registosFFS)).start();

            byte[] pacote = new byte[1024];

            while (true) {
                DatagramPacket pacoteDeResgisto = new DatagramPacket(pacote, pacote.length);
                s.receive(pacoteDeResgisto);
                PacoteRegisto pr = PacoteRegisto.descompactar(pacote);

                this.registosFFS.adiciona(pr.origem,pr.ficheiros);

                System.out.println("@ Efetuado registo de: " + pr.origem.toString());

                new ConexaoFiavel(s).enviaACK(pr.origem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" >>> GatewayWorkerRegister error!");
        }
    }
}
