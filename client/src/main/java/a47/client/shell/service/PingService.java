package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import org.jboss.logging.Logger;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class PingService {
    private Logger logger = Logger.getLogger(PingService.class);

    private SortedMap<Integer, String> replicas;

    private TimerTask pingThread;

    private Timer timer;

    private ClientShell shell;

    public class PingThread extends TimerTask {
        int tries = 0;

        @Override
        public void run() {
            logger.debug("Ping Primary Server: " + Constants.SERVER_URL);
            RestTemplate restTemplate = new RestTemplate();
            try {
                replicas = restTemplate.getForObject(Constants.SERVER.PING_SERVER_URL, TreeMap.class);
                logger.debug("Primary server is alive");
            }
            catch(ResourceAccessException rae) {
                tries++;
                logger.debug("Couldn't ping primary server");
            }

            if(tries == Constants.SERVER.NUMBER_TRY){
                if(!replicas.isEmpty()) {
                    logger.debug("Reached max number of ping tries, logging out");
                    tries = 0;
                    AuxMethods.logout(shell);
                    shell.println("Server that you are connected was compromised. Please login again!");
                    String newServerURL = replicas.get(replicas.firstKey());
                    replicas.remove(replicas.firstKey());
                    Constants.SERVER_URL = newServerURL;
                    Constants.reviewServerURLS();
                    logger.debug("New server: " + newServerURL);
                    logger.debug("Login to use the the file sharing system");
                }
            }
        }
    }

    public PingService(ClientShell shell){
        this.shell = shell;
        this.replicas = new TreeMap<>();
        this.pingThread = new PingThread();
        this.timer = new Timer();

        timer.scheduleAtFixedRate(pingThread, 0, Constants.SERVER.TIMEOUT_PING);
    }
}
