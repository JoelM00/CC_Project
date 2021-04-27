import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Controlador {
    String ficheiroPedido;
    RegistosFFS registosFFS;
    HttpWorker.PacoteFicheiro pacoteFicheiro;

    public Controlador(String ficheiroPedido, RegistosFFS registosFFS, HttpWorker.PacoteFicheiro pacoteFicheiro) {
        this.ficheiroPedido = ficheiroPedido;
        this.registosFFS = registosFFS;
        this.pacoteFicheiro = pacoteFicheiro;
    }

    public void ordenaPacotes() {
        List<PacoteChunk> ordenada = new ArrayList<>();
        Map<Integer,PacoteChunk> aux = new HashMap<>();
        try {
            this.pacoteFicheiro.l.lock();
            for (PacoteChunk p : this.pacoteFicheiro.pacotes) {
                aux.put(p.inicio,p);
            }

            List<Integer> aux2 = aux.keySet().stream().sorted().collect(Collectors.toList());

            for (Integer i : aux2) {
                ordenada.add(aux.get(i));
            }
            pacoteFicheiro.pacotes = ordenada;

        } finally {
            this.pacoteFicheiro.l.unlock();
        }
    }

    public void validaPacote(int metaDados) {
        int size = 0;
        for (PacoteChunk p : this.pacoteFicheiro.pacotes) {
            size += p.dados.length;
        }
        if (size != metaDados) {
            System.out.println(" >>> Pacote incompleto! Algum FFS morreu!");
            this.pacoteFicheiro.pacotes.clear();
        }
    }

    public List<Tupulo> procuraFFS() {
        List<Tupulo> ffsComFicheiro = new ArrayList<>();
        try {
            registosFFS.rl.lock();
            for (Map.Entry<Tupulo,List<String>> ffs : registosFFS.registosFFS.entrySet()) {
                for (String s : ffs.getValue()) {
                    if (s.equals(ficheiroPedido)) {
                        ffsComFicheiro.add(ffs.getKey());
                    }
                }
            }
            return ffsComFicheiro;
        } finally {
            registosFFS.rl.unlock();
        }
    }

    public int trataPedido() throws IOException, InterruptedException {

        List<Tupulo> ffsComFicheiro = procuraFFS();

        if (ffsComFicheiro.size() == 0) return 1;

        if (ffsComFicheiro.size() == 1 ) {                                      // || || pr.tamanhoFicheiro < 1000

            System.out.println(" -> Acionada um busca por um FFS");
            new GatewayWorkerFFS(ffsComFicheiro.get(0), registosFFS, ficheiroPedido, pacoteFicheiro,0,0).run();

        } else {
            System.out.println(" -> Acionada um busca por varios FFS");

            DatagramSocket s = new DatagramSocket(0);
            DatagramSocket r = new DatagramSocket(0);
            Tupulo origem = new Tupulo(s.getLocalAddress(), s.getLocalPort());
            Tupulo origemR = new Tupulo(r.getLocalAddress(),r.getLocalPort());
            byte[] pacote = new byte[1024];

            //Pede metadados!
            Thread aQueVaiEnviar = new Thread(() -> {
                try {
                    byte[] pacote1 = new byte[1024];
                    PacotePedido pp = new PacotePedido(origemR, origem, ficheiroPedido, true,false,0,0);
                    pacote1 = PacotePedido.compactar(pp);
                    DatagramPacket pedidoDeFicheiro = new DatagramPacket(pacote1, pacote1.length, ffsComFicheiro.get(0).endereco, ffsComFicheiro.get(0).porta);

                    new ConexaoFiavel(s,pedidoDeFicheiro).envia();
                } catch (Exception e) {
                    System.out.println("oLA!");
                }
            });

            aQueVaiEnviar.start();

            //Recebe metadados
            DatagramPacket pacoteDeResp = new DatagramPacket(pacote, pacote.length);
            r.receive(pacoteDeResp);
            PacoteResposta pr = PacoteResposta.descompactar(pacote);

            aQueVaiEnviar.stop();


            int fatia = pr.tamanhoFicheiro / ffsComFicheiro.size();
            int offset = 0,anterior;
            List<Thread> thrs = new ArrayList<>();


            for (int i = 0; i < ffsComFicheiro.size(); i++) {
                anterior = offset;
                offset += fatia;

                int finalAnterior = anterior;
                int finalOffset = offset;
                int finalI = i;
                thrs.add(new Thread(() -> new GatewayWorkerFFS(ffsComFicheiro.get(finalI),registosFFS,ficheiroPedido,pacoteFicheiro,finalAnterior,finalOffset).run()));
            }
            if (pr.tamanhoFicheiro % ffsComFicheiro.size() != 0) {
                int finalOffset1 = offset;
                thrs.add(new Thread(() -> new GatewayWorkerFFS(ffsComFicheiro.get(ffsComFicheiro.size() - 1), registosFFS, ficheiroPedido, pacoteFicheiro, finalOffset1, pr.tamanhoFicheiro).run()));
            }

            for (Thread t : thrs) {
                t.start();
            }
            for (Thread t : thrs) {
                t.join();
            }

            ordenaPacotes();
            validaPacote(pr.tamanhoFicheiro); //se o pacote n estiver completo, apaga tudo!
        }
        return 0;
    }
}
