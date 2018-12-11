package a47.client;

public class Constants {

    public static final String CA_URL = "https://localhost:30000";
    public static String SERVER_URL = "https://localhost:30001";
    public static final String FILE_SERVER_URL = "/files";


    public static final class Keys{
        public static final String CA_KEYSTORE_CIPHER = "RSA";
        public static final String CA_CIPHER = "RSA/ECB/PKCS1Padding";
        public final static String SYMMETRIC_ALGORITHM = "AES/CBC/PKCS5Padding";
        public static final String KEYS_LOCATION = "/var/.keys/";
    }

    public static final class CA{
        public static final String PUBLISH_URL = CA_URL + "/publish";
        public static final String PUBLISH_RESPONSE_URL = CA_URL + "/publish/response";
        public static final String REQUEST_URL = CA_URL + "/request";
    }

    public static final class SERVER{
        public static String REGISTER_SERVER_URL = SERVER_URL + "/auth/register";
        public static String REGISTER_RESPONSE_SERVER_URL = SERVER_URL + "/auth/register/response";
        public static String LOGIN_SERVER_URL = SERVER_URL + "/auth/login";
        public static String LOGIN_RESPONSE_SERVER_URL = SERVER_URL + "/auth/login/response";
        public static String LOGOUT_SERVER_URL = SERVER_URL + "/auth/logout";
        public static String PING_SERVER_URL = SERVER_URL + "/ping/ping";
        public static final int NUMBER_TRY = 3;
        public static final int TIMEOUT_PING = 5 * 1000;
    }

    public static final class FILE{
        public static String UPLOAD_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/upload";
        public static String LIST_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/listUserFiles";
        public static String DOWNLOAD_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/download";
        public static String SHARE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/share";
        public static String UNSHARE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/unshare";
        public static String CHECK_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/check";
        public static String GETACCESS_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/listAccessFileUser";
        public static String UPDATE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/update";
        public static String INFO_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/fileInfo";
        public static final int IV_SIZE = 16;
        public static final int SALT_SIZE = 16;
        public static final int CIPHERED_HASH_SIZE = 256;
        public final static String SYMMETRIC_ALGORITHM = "AES";
        public final static String SYMMETRIC_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";
        public final static int SYMMETRIC_SIZE = 32; // This gives a key of 32 byte * 8 = 256 bits
    }

    public static void reviewServerURLS(){
        SERVER.REGISTER_SERVER_URL = SERVER_URL + "/auth/register";
        SERVER.REGISTER_RESPONSE_SERVER_URL = SERVER_URL + "/auth/register/response";
        SERVER.LOGIN_SERVER_URL = SERVER_URL + "/auth/login";
        SERVER.LOGIN_RESPONSE_SERVER_URL = SERVER_URL + "/auth/login/response";
        SERVER.LOGOUT_SERVER_URL = SERVER_URL + "/auth/logout";
        SERVER.PING_SERVER_URL = SERVER_URL + "/ping/ping";
        FILE.UPLOAD_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/upload";
        FILE.LIST_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/listUserFiles";
        FILE.DOWNLOAD_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/download";
        FILE.SHARE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/share";
        FILE.UNSHARE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/unshare";
        FILE.CHECK_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/check";
        FILE.GETACCESS_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/listAccessFileUser";
        FILE.UPDATE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/update";
        FILE.INFO_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/fileInfo";

    }

}
