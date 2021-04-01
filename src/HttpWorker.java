
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class HttpWorker implements Runnable {
    private Socket s;
    private BufferedReader in;
    private PrintWriter out;

    public HttpWorker(Socket s) throws IOException {
        this.s = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new PrintWriter(s.getOutputStream());
    }

    @Override
    public void run() {
        try {
            String get = in.readLine();
            String[] tokens = get.split(" ", 3);
            String ficheiro = tokens[1].substring(1);

            File f = new File("./ficheiros/"+ficheiro);
            InputStream file = new FileInputStream(f);

            Scanner sc = new Scanner(f);

            int amountBytesToRead = (int) f.length();
            int positionToRead = 0;
            FileInputStream fis = new FileInputStream("./ficheiros/"+ficheiro);

            //A direct ByteBuffer should be slightly faster than a 'normal' one for IO-Operations
            ByteBuffer bytes = ByteBuffer.allocateDirect(amountBytesToRead);
            fis.getChannel().read(bytes, positionToRead);

            byte[] readBytes = bytes.array();

            System.out.println(new String(readBytes));
            //Handle Bytes


            String httpResp = "HTTP/1.1 200 OK\n" +
                    "Server: Apache\n" +
                    "Content-Length: 187\n" +
                    "Content-Type: text\n" +
                    "\n";

            out.print(httpResp);
            out.flush();
            while (sc.hasNext()) {
                out.println(sc.nextLine());
                out.flush();
            }
        } catch (Exception e) {}
    }
}
