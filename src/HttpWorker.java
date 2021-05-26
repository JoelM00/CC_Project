import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class HttpWorker implements Runnable {
    public static class PacoteFicheiro {
        List<PacoteChunk> pacotes = new ArrayList<>();
        ReentrantLock l = new ReentrantLock();
    }

    private Socket s;
    private BufferedReader in;
    private PrintWriter out;
    private RegistosFFS registosFFS;
    InetAddress endereco;

    public HttpWorker(InetAddress endereco, Socket s, RegistosFFS registosFFS) throws IOException {
        this.s = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new PrintWriter(s.getOutputStream());
        this.registosFFS = registosFFS;
        this.endereco = endereco;
    }

    @Override
    public void run() {
        try {

            String linha, ficheiroPedido = "";
            for (int i = 0; i < 7; i++) {
                linha = in.readLine();
                if (i == 0) {
                    String[] tokens = linha.split(" ", 3);
                    ficheiroPedido = tokens[1].substring(1);
                }
            }

            PacoteFicheiro pf = new PacoteFicheiro();
            new Controlador(endereco,ficheiroPedido,registosFFS,pf).trataPedido();

            if (pf.pacotes.size() != 0) {
                System.out.println("\n# Busca bem sucedida! #");

                int size = 0;
                for (PacoteChunk p : pf.pacotes) {
                    size += p.dados.length;
                }

                String httpResp = "HTTP/1.1 200 OK\n" +
                        "Server: Apache\n" +
                        "Content-Length: " + size + "\n" +
                        "Content-Type: text\n" +
                        "\n";

                //Manda o cabecalho
                out.print(httpResp);
                out.flush();
                //Manda os pacotes do ficheiro
                for (PacoteChunk pc : pf.pacotes) {
                    out.print(new String(pc.dados));
                    out.flush();
                }
                out.flush();
            } else {
                System.out.println("\n# Busca mal sucedida! #");
                String httpResp = "HTTP/1.1 404\n" +
                        "Server: Apache\n" +
                        "Content-Type: text\n" +
                        "\n";
                out.print(httpResp);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" >>> HttpWorker error!");
        }
    }
}