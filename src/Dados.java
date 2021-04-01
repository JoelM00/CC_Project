import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Dados {
    public List<String> ficheiros;
    public long from;
    public long to;
    public int portaDoFFS;
    public int portaDoGW;
    public byte[] conteudo;
    public Boolean pedidoDeRegisto;
    public String fileName;


    public static byte[] read(String file, long from, long to) throws Exception {
        System.out.println("\n\nLendo de "+from +" ate "+to);

        FileInputStream fin = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(fin);

        int tamanhoPacote = (int) (to-from);

        byte[] buffer = new byte[tamanhoPacote];
        try {
            bin.skip(from);
            bin.read(buffer,0,tamanhoPacote);
            System.out.print(new String(buffer));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bin.close();
        }
        return buffer;
    }

    public static byte[] readField(byte[] dados, int from, int to) {

        int tamanhoPacote = to - from;
        byte[] buffer = new byte[tamanhoPacote];

        for (int i = from; i < to; i++) {
            buffer[i] = dados[i];
        }

        return buffer;
    }


    //Tem valores aleatorios para os campos
    public static Dados descompactar(DatagramPacket pacote) {
        Dados d = new Dados();
        int tamanhoNomeFicheiro = 50;
        d.pedidoDeRegisto = Boolean.getBoolean(new String(readField(pacote.getData(),0,5)));
        d.from = Integer.getInteger(new String(readField(pacote.getData(),5,10)));
        d.to = Integer.getInteger(new String(readField(pacote.getData(),10,15)));
        d.portaDoFFS = Integer.getInteger(new String(readField(pacote.getData(),15,20)));
        d.portaDoGW = Integer.getInteger(new String(readField(pacote.getData(),20,25)));
        if (d.pedidoDeRegisto) {
            int inicio = 5;
            int fim = inicio+tamanhoNomeFicheiro;
            String s = new String(readField(pacote.getData(),inicio,fim));
            while (!s.equals("TERMINADO")) {
                d.ficheiros.add(s);
                inicio = fim;
                fim += tamanhoNomeFicheiro;
                s = new String(readField(pacote.getData(),inicio,fim));
            }
        } else {
            //No caso de n ser um pedido de registo, a lista de ficheiros tera apenas uma string, o nome do ficheiro pedido
            d.fileName = new String(readField(pacote.getData(),30,30+tamanhoNomeFicheiro));
        }

        return d;
    }

    public static byte[] compactar(Dados dados) {
        byte[] pacote = new byte[100];
        List<byte[]> componentes = new ArrayList<>();
        //Ler dados... Estou com problemas nos tipos

        return pacote;
    }

}
