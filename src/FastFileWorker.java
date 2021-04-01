import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FastFileWorker implements Runnable {
    private DatagramSocket s;
    private String fileName;
    private long from;
    private long to;
    private long maxPacote = 50;
    private int portaDoGW;
    private InetAddress enderecoDoGW;
    private Map<Integer,byte[]> pacotes;

    public FastFileWorker(String fileName, long from, long to, DatagramSocket s, int portaDoGW, InetAddress enderecoDoGW) {
        this.s = s;
        this.fileName = fileName;
        this.from = from;
        this.to = to;
        this.portaDoGW = portaDoGW;
        this.enderecoDoGW = enderecoDoGW;
        this.pacotes = new HashMap<>();
    }

    private void send() throws IOException {

        byte[] conteudo = new byte[1024];
        conteudo = "OLA MEN".getBytes();

        DatagramPacket pac = new DatagramPacket(conteudo,conteudo.length,enderecoDoGW,6000);

        s.send(pac);

/*
        for (Map.Entry<Integer,byte[]> p : this.pacotes.entrySet()) {
            //Inicia timeout
            //a fazer
            byte[] conteudo = p.getValue();
            DatagramPacket pacote = new DatagramPacket(conteudo,conteudo.length,endereco,portaDoGW);
            s.send(pacote);
            //Espera ACK
            //s.receive();
        }*/
    }

    @Override
    public void run() {
        try {
            /*File file = new File(this.fileName);

            if (file.exists()) {
                long bytes = file.length();
                System.out.println("Tamanho do ficheiro é: "+bytes+ "bytes");
                long parts = bytes / maxPacote;
                System.out.println("São "+parts + " pacotes");

                for (int i=0; i <= parts; i++){
                    long ofSet = i * maxPacote;
                    long limit = ofSet + maxPacote;
                    pacotes.put(i, Dados.read(fileName,ofSet,limit));
                }

                System.out.println("\n\nFinal Resultado:");
                for(byte[] b : pacotes.values()){
                    System.out.print(new String(b));
                }
            } else {
                System.out.println("File Not Found");
            }*/

            send();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FastWorker error!");
        }
    }
}
