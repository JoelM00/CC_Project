import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpGW {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5000);
        HttpWorker w;

        // 192.168.1.71
        InetAddress ip = Inet4Address.getLocalHost();
        //System.out.println(ip);

        while (true) {
            Socket s = ss.accept();
            new Thread(new HttpWorker(s)).start();
        }
    }
}
