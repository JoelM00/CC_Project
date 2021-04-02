
import java.io.*;
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

    public HttpWorker(Socket s,int portaParaComunicacaoGW_FFS,Map<Tupulo, List<String>> registosFFS) throws IOException {
        this.s = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new PrintWriter(s.getOutputStream());
        this.portaParaComunicacaoGW_FFS = portaParaComunicacaoGW_FFS;
        this.registosFFS = registosFFS;
    }


    @Override
    public void run() {
        try {
            String linha, ficheiroPedido = "nada.txt", enderecoOrigem = "0.0.0.0", portaOrigem = "-1";
            for (int i = 0; i < 7; i++) {
                linha = in.readLine();
                if (i == 0) {
                    String[] tokens = linha.split(" ", 3);
                    ficheiroPedido = tokens[1].substring(1);
                }
                if (i == 4) {
                    String[] tokens1 = linha.split(" ", 2);
                    String[] tokens2 = tokens1[1].split(":", 2);
                    enderecoOrigem = tokens2[0];
                    portaOrigem = tokens2[1];
                }
            }

            //Procura os dados do FFS que tem o ficheiro pedido
            List<byte[]> pacoteComFicheiroPedido = new ArrayList<>();
            Tupulo dadosDoFFS;

            for (Map.Entry<Tupulo, List<String>> registos : this.registosFFS.entrySet()) {
                for (String s : registos.getValue()) {
                    if (s.equals(ficheiroPedido)) {
                        dadosDoFFS = registos.getKey();
                        new Thread(new GatewayWorkerFFS(dadosDoFFS,ficheiroPedido,pacoteComFicheiroPedido)).start();
                        break;
                    }
                }
            }

            String httpResp = "HTTP/1.1 200 OK\n" +
                    "Server: Apache\n" +
                    "Content-Length: 187\n" +
                    "Content-Type: text\n" +
                    "\n";

            out.print(httpResp);
            out.flush();
            for (byte[] s : pacoteComFicheiroPedido) {
                out.println(new String(s));
                out.flush();
            }

        } catch (Exception e) {}
    }
}
