import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ConexaoFiavel {
    DatagramSocket socket;
    DatagramPacket pacoteAEnviar;
    int limite = 4;

    public ConexaoFiavel(DatagramSocket socket, DatagramPacket pacoteAEnviar) {
        this.socket = socket;
        this.pacoteAEnviar = pacoteAEnviar;
    }

    public ConexaoFiavel(DatagramSocket socket) {
        this.socket = socket;
    }

    public int envia() throws InterruptedException, IOException {
        byte[] pacote = new byte[1024];
        DatagramPacket ack = new DatagramPacket(pacote,pacote.length);

        Thread t = null;

        for (int i = 0;  i < limite; i++) {
            socket.send(pacoteAEnviar);

            t = new Thread(() -> {
                try {
                    socket.receive(ack);
                } catch (Exception e) {
                    System.out.println(" >>> Socket fechado (Gateway <-> FFS)!");
                }
            });

            t.start();
            t.join(500);

            PacoteACK p = null;
            try {
                p = PacoteACK.descompactar(pacote);
                if (p.codigo != 1) {    //Uma especie de checksum
                    System.out.println(" -> ACK invalido!");
                    t.stop();
                } else {
                    //System.out.println("$$ Pacote entregue! $$");
                    return 0;
                }
            } catch (Exception e) {
                System.out.println(" >>> Erro ao descompactar pacote ACK");
                t.stop();
            }
        }
        System.out.println(" >>> Limite excedido!");
        socket.close();
        return 1;
    }

    public void enviaACK(Tupulo destino) throws IOException {
        byte[] pacote = new byte[1024];
        PacoteACK pacoteAck = new PacoteACK(1);
        pacote = PacoteACK.compactar(pacoteAck);
        DatagramPacket pacACK = new DatagramPacket(pacote,pacote.length,destino.endereco,destino.porta);
        socket.send(pacACK);
    }

    public PacoteChunk recebeChunk(Tupulo origem) throws InterruptedException {
        byte[] pacote = new byte[1024];
        DatagramPacket chunk = new DatagramPacket(pacote,pacote.length);

        Thread t = null;

        for (int i = 0;  i < limite; i++) {
            t = new Thread(() -> {
                try {
                    socket.receive(chunk);
                } catch (Exception e) {
                    System.out.println(" >>> Socket fechado (Gateway <- FFS)!");
                }
            });

            t.start();
            t.join(1000);

            PacoteChunk p = null;
            try {
                p = PacoteChunk.descompactar(pacote);
                if (!p.destino.equals(origem)) {              //Uma especie de checksum
                    System.out.println(" -> Chunk invalida " + i);
                    t.stop();
                } else {
                    //System.out.println("$$ Chunk recebida $$");
                    return p;
                }
            } catch (Exception e) {
                System.out.println(" >>> Erro ao descompactar pacote CHUNK");
                t.stop();
            }
        }
        System.out.println(" >>> Limite excedido!");
        socket.close();
        return null;
    }
}
