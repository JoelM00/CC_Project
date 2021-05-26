import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class FastFileWorker implements Runnable {
    String fileName;
    int from;
    int to;
    long maxPacote = 500;
    Map<Integer,byte[]> pacotes;
    Tupulo destino;
    InetAddress endereco;

    public FastFileWorker(InetAddress endereco, String fileName,Tupulo destino,int from, int to) {
        this.fileName = fileName;
        this.pacotes = new HashMap<>();
        this.destino = destino;
        this.from = from;
        this.to = to;
        this.endereco = endereco;
    }

    private void send() throws IOException, InterruptedException {
        DatagramSocket s = new DatagramSocket(0);
        Tupulo origem = new Tupulo(endereco,s.getLocalPort());

        byte[] pacote = new byte[1024];
        int offset = from, offsetAnterior;

        int size = this.pacotes.keySet().size();

        for (Map.Entry<Integer,byte[]> p : this.pacotes.entrySet()) {
            byte[] conteudo = p.getValue();

            offsetAnterior = offset;
            offset += conteudo.length;

            PacoteChunk pc;
            if (p.getKey() == size - 1) {
                pc = new PacoteChunk(conteudo,offsetAnterior,offset,true,destino,origem);
            } else {
                pc = new PacoteChunk(conteudo,offsetAnterior,offset,false,destino,origem);
            }

            pacote = PacoteChunk.compactar(pc);
            DatagramPacket pacoteComUmChunk = new DatagramPacket(pacote,pacote.length,destino.endereco,destino.porta);

            int codigo = new ConexaoFiavel(s,pacoteComUmChunk).envia();
            if (codigo != 0) {
                System.out.println(" >>> Gateway deixou de responder!");
                return;
            }

            System.out.println("# "+(p.getKey()+1)+" pacoteChunk enviado!");
        }
        System.out.println(" --> Ficheiro enviado!");
    }

    @Override
    public void run() {
        try {
            File file = new File(this.fileName);
            long bytes;
            if ((to-from) == 0) {
                bytes = file.length();
            } else {
                bytes = to - from;
                System.out.println(bytes);
            }

            if (file.exists()) {
                if ((to-from) == 0) {
                    long parts = bytes / maxPacote;

                    for (int i = 0; i <= parts; i++) {
                        long ofSet = i * maxPacote;
                        long limit = ofSet + maxPacote;

                        pacotes.put(i, Dados.read(fileName, ofSet, limit));
                    }
                } else {
                    long parts = bytes / maxPacote;
                    System.out.println("Partes: "+parts);

                    for (int i = 0; i <= parts; i++) {
                        long ofSet = from + i * maxPacote;
                        long limit = ofSet + maxPacote;

                        //Tentativa de resolver bug!?
                        if (limit > to) {
                            limit = to;
                        }

                        pacotes.put(i, Dados.read(fileName, ofSet, limit));
                    }
                }
            } else {
                System.out.println(" -> File Not Found");
            }

            send();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" -> FastWorker error!");
        }
    }
}
