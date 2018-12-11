package a47.server;

public class Constants {

    public static String SERVER_URL;
    public static final String CA_URL = "https://localhost:30000";
    public static String PRIM_SERVER_URL = "https://localhost:30001";
    public static final String SEC_SERVER_BASE_URL = "https://localhost:";

    public static final class SERVER{
        public static String REGISTER_SERVER_URL = PRIM_SERVER_URL + "/repli/registerReplica";
        public static String PING_SERVER_URL = PRIM_SERVER_URL + "ping/ping";
        public static final int REPLICATION_TIME= 15 * 1000;

        public static void reviewServerURLS(){
            REGISTER_SERVER_URL = PRIM_SERVER_URL + "/repli/registerReplica";
            PING_SERVER_URL = PRIM_SERVER_URL + "ping/ping";
        }
    }

    public static final class SEC{
        public static String TURNPRIM_SEC_URL(int port){
            return SEC_SERVER_BASE_URL + port + "/repli/turnPrimary";
        }
        public static String CHANGEPRIM_SEC_URL(int port){
            return SEC_SERVER_BASE_URL + port + "/repli/changePrimary";
        }
    }

    public static final class DIRECTORY{
        public static String FILES_DIRECTORY;
    }
}
