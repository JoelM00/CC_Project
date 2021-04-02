import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class FastFileWorker implements Runnable {
    private DatagramSocket s;
    private String fileName;
    private long from; //tentamos primeiro fazer o envio de todas as chunks de um so servidor
    private long to;
    private long maxPacote = 50;
    private Map<Integer,byte[]> pacotes;
    private Tupulo origem;
    private Tupulo destino;


    public FastFileWorker(DatagramSocket s, String fileName, Tupulo destino, Tupulo origem) {
        this.s = s;
        this.fileName = fileName;
        this.pacotes = new HashMap<>();
        this.destino = destino;
        this.origem = origem;
    }

    private void send() throws IOException {

        byte[] pacote = new byte[1024];
        long offset = 0, offsetAnterior;

        for (Map.Entry<Integer,byte[]> p : this.pacotes.entrySet()) {
            //Inicia timeout ...
            byte[] conteudo = p.getValue();

            offsetAnterior = offset;
            offset += conteudo.length;

            PacoteChunk pc = new PacoteChunk(conteudo,offsetAnterior,offset,destino);

            pacote = PacoteChunk.compactar(pc);

            DatagramPacket pacoteComUmChunk = new DatagramPacket(pacote,pacote.length,destino.endereco,destino.porta);

            System.out.println("Pacote enviado!");
            s.send(pacoteComUmChunk);

            //Espera ACK ...
            //s.receive();
        }
    }

    @Override
    public void run() {
        try {
            //Carrega o Map com os pacotes do ficheiro
            File file = new File(this.fileName);

            if (file.exists()) {
                long bytes = file.length();
                long parts = bytes / maxPacote;

                for (int i=0; i <= parts; i++){
                    long ofSet = i * maxPacote;
                    long limit = ofSet + maxPacote;
                    pacotes.put(i, Dados.read(fileName,ofSet,limit));
                }

                for(byte[] b : pacotes.values()){
                    System.out.print(new String(b));
                }
            } else {
                System.out.println("File Not Found");
            }

            send();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FastWorker error!");
        }
    }
}
