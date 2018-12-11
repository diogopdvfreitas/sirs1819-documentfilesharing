package a47.server.service;

import a47.server.Constants;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

@Service
public class PingService {
    private Logger logger = Logger.getLogger(PingService.class);

    private SortedMap<Integer, String> replicas;

    private TimerTask pingThread;

    private Timer timer;

    public class PingThread extends TimerTask {
        @Override
        public void run() {
            logger.info("Ping Primary Server: " + Constants.PRIM_SERVER_URL);
            RestTemplate restTemplate = new RestTemplate();
            try {
                replicas = restTemplate.getForObject(Constants.SERVER.PING_SERVER_URL, TreeMap.class);
                logger.info("Primary server is alive");
            }
            catch(ResourceAccessException rae) {
                logger.info("Couldn't ping primary server");
            }
        }
    }

    public PingService() {
        this.replicas = new TreeMap<>();
    }

    public SortedMap<Integer, String> getReplicas() {
        return replicas;
    }

    public void init() {
        this.pingThread = new PingThread();
        this.timer = new Timer();

        timer.scheduleAtFixedRate(pingThread, 0, 30*1000);
    }

    public void stop(){
        timer.cancel();
    }
}
