import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RegistosFFS {
    public Map<Tupulo,List<String>> registosFFS;
    private ReentrantReadWriteLock l;
    private Lock wl;
    public Lock rl;

    public RegistosFFS() {
        this.registosFFS = new HashMap<>();
        this.l = new ReentrantReadWriteLock();
        this.wl = l.writeLock();
        this.rl = l.readLock();
    }

    public void remove(Tupulo key) {
        try {
            this.wl.lock();
            this.registosFFS.remove(key);
        } finally {
            this.wl.unlock();
        }
    }

    public void adiciona(Tupulo dados, List<String> ficheiros) {
        try {
            this.wl.lock();
            this.registosFFS.put(dados,ficheiros);
        } finally {
            this.wl.unlock();
        }
    }
}
