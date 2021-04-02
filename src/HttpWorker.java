import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpWorker implements Runnable {
    private Socket s;
    private BufferedReader in;
    private PrintWriter out;
    private int portaParaComunicacaoGW_FFS;
    private Map<Tupulo, List<String>> registosFFS;

    public HttpWorker(Socket s,  int portaParaComunicacaoGW_FFS, Map<Tupulo, List<String>> registosFFS) throws IOException {
        this.s = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new PrintWriter(s.getOutputStream());
        this.portaParaComunicacaoGW_FFS = portaParaComunicacaoGW_FFS;
        this.registosFFS = registosFFS;
    }

    @Override
    public void run() {
        try {
/*
            String linha, ficheiroPedido = "e so para inicializar", enderecoCliente = "e so para inicializar", portaCliente = "e so para inicializar";
            for (int i = 0; i < 7; i++) {
                linha = in.readLine();
                if (i == 0) {
                    String[] tokens = linha.split(" ", 3);
                    ficheiroPedido = tokens[1].substring(1);
                }
                if (i == 4) {
                    String[] tokens1 = linha.split(" ", 2);
                    String[] tokens2 = tokens1[1].split(":", 2);
                    enderecoCliente = tokens2[0];
                    portaCliente = tokens2[1];
                }
            }*/
            String ficheiroPedido = "ola.txt";

            //Procura os dados do FFS que tem o ficheiro pedido
            List<PacoteChunk> pacoteComFicheiroPedido = new ArrayList<>(); //estado partilhado
            Tupulo dadosDoFFS;

            for (Map.Entry<Tupulo, List<String>> registos : this.registosFFS.entrySet()) {
                for (String s : registos.getValue()) {
                    if (s.equals(ficheiroPedido)) {
                        dadosDoFFS = registos.getKey();
                        new Thread(new GatewayWorkerFFS(dadosDoFFS,ficheiroPedido,pacoteComFicheiroPedido,portaParaComunicacaoGW_FFS)).start();
                        break;
                    }
                }
            }
            //Temos de arranjar maneira desta thread parar ate que as threads que criar la em cima terminem, e assim seja garantido que "pacoteComFicheiroPedido" esta completo
            System.out.println("Se paciente! faltam 5s... ");
            Thread.sleep(5000);
            for (PacoteChunk p : pacoteComFicheiroPedido) {
                System.out.print(new String(p.dados));
            }
            System.out.println("\nVou mandar ao cliente!");

            int size = 0;
            for (PacoteChunk p : pacoteComFicheiroPedido) {
                size += p.dados.length;
            }

            String httpResp = "HTTP/1.1 200 OK\n" +
                    "Server: Apache\n" +
                    "Content-Length: "+size+"\n" +
                    "Content-Type: text\n" +
                    "\n";

            //Manda o cabecalho
            out.print(httpResp);
            out.flush();
            //Manda os pacotes do ficheiro
            for (PacoteChunk pc : pacoteComFicheiroPedido) {
                out.print(new String(pc.dados));
                out.flush();
            }
            out.flush();

        } catch (Exception e) {
            System.out.println("HttpWorker error!");
        }
    }
}