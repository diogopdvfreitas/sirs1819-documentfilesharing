package a47.server.service;

import a47.server.Constants;
import a47.server.exception.ErrorMessage;
import a47.server.exception.ReplicaAlreadyExists;
import a47.server.model.File;
import a47.server.model.replication.ServerSnapshot;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ReplicationService {
    private static Logger logger = Logger.getLogger(ReplicationService.class);

    private AuthenticationService authenticationService;

    private FileManagerService fileManagerService;

    private FileStorageService fileStorageService;

    private PingService pingService;

    private SortedMap<Integer, String> registeredReplicas;

    private List<ServerSnapshot> replicationHistory;

    private TimerTask replicationThread;

    private Timer timer;

    public class ReplicationThread extends TimerTask {
        @Override
        public void run() {
            RestTemplate restTemplate;
            setRegisteredReplicas();
            if(fileManagerService.checkFilesIntegrity()){
                logger.info("Server compromised! Initiating shutdown");
                if(!registeredReplicas.isEmpty()) {
                    int newPrimServerPort = registeredReplicas.firstKey();
                    String newPrimServerURL = registeredReplicas.get(newPrimServerPort);
                    registeredReplicas.remove(newPrimServerPort);
                    restTemplate = new RestTemplate();
                    if (restTemplate.getForObject(Constants.SEC.TURNPRIM_SEC_URL(newPrimServerPort), Boolean.class)) {
                        for (int port : registeredReplicas.keySet()) {
                            String url = Constants.SEC.CHANGEPRIM_SEC_URL(port);
                            restTemplate.postForObject(url, newPrimServerURL, Boolean.class);
                        }
                        logger.info("Server will now shutdown");
                        System.exit(0);
                    }
                }
                else {
                    logger.info("Server will now shutdown");
                    System.exit(0);
                }
            }
            logger.info("Creating Server Snapshot");
            List<File> files = new ArrayList<>();
            for(String fileId : fileManagerService.getFilesMetaData().keySet()) {
                files.add(fileStorageService.getFile(fileId));
            }
            ServerSnapshot snapshot = new ServerSnapshot(authenticationService.getRegisteredUsers(),
                                                         fileManagerService.getUserFiles(),
                                                         fileManagerService.getFilesMetaData(),
                                                         files);
            restTemplate = new RestTemplate();
            for(String url : registeredReplicas.values()) {
                logger.info("Sending Server Snapshot to " + url);
                restTemplate.postForObject(url + "/repli/replicatePrimary", snapshot, Boolean.class);
            }
        }
    }

    public ReplicationService(AuthenticationService authenticationService, FileManagerService fileManagerService, FileStorageService fileStorageService, PingService pingService) {
        this.authenticationService = authenticationService;
        this.fileManagerService = fileManagerService;
        this.fileStorageService = fileStorageService;
        this.pingService = pingService;
        this.registeredReplicas = new TreeMap<>();
        this.replicationHistory = new ArrayList<>();
    }

    public SortedMap<Integer, String> getRegisteredReplicas() {
        return registeredReplicas;
    }

    public void setRegisteredReplicas() {
        this.registeredReplicas = pingService.getReplicas();
    }

    public void registerReplica(String url){
        if(registeredReplicas.containsValue(url)){ //check if replica already exists
            throw new ReplicaAlreadyExists(ErrorMessage.CODE_SERVER_DUP_REPLICA, "Replica already exists");
        }
        String[] split = url.split(":");
        registeredReplicas.put(Integer.parseInt(split[2]), url);
    }

    public void initReplication() {
        this.replicationThread = new ReplicationThread();
        this.timer = new Timer(true);

        timer.scheduleAtFixedRate(replicationThread, 0, Constants.SERVER.REPLICATION_TIME);
    }

    public void replicatePrimary(ServerSnapshot snapshot) {
        authenticationService.setRegisteredUsers(snapshot.getRegisteredUsers());
        fileManagerService.setUserFiles(snapshot.getUserFiles());
        fileManagerService.setFilesMetaData(snapshot.getFilesMetaData());
        for(File file : snapshot.getFiles()){
            fileStorageService.saveFile(file);
            fileManagerService.generateFileHashes(file);
        }
        replicationHistory.add(snapshot);
    }

    public void turnPrimary(){
        Constants.PRIM_SERVER_URL = Constants.SERVER_URL;
        int port = Integer.parseInt(Constants.SERVER_URL.split(":")[2]);
        registeredReplicas.remove(port);
        pingService.stop();
        initReplication();
    }

    public void changePrimary(String url){
        Constants.PRIM_SERVER_URL = url;
        Constants.SERVER.reviewServerURLS();
        int port = Integer.parseInt(Constants.SERVER_URL.split(":")[2]);
        registeredReplicas.remove(port);
    }
}
