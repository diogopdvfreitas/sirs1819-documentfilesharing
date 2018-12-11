package a47.client;

public class Constants {

    public static final String CA_URL = "https://localhost:30000";
    public static final String SERVER_URL = "https://localhost:30001";
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
        public static final String REGISTER_SERVER_URL = SERVER_URL + "/auth/register";
        public static final String REGISTER_RESPONSE_SERVER_URL = SERVER_URL + "/auth/register/response";
        public static final String LOGIN_SERVER_URL = SERVER_URL + "/auth/login";
        public static final String LOGIN_RESPONSE_SERVER_URL = SERVER_URL + "/auth/login/response";
        public static final String LOGOUT_SERVER_URL = SERVER_URL + "/auth/logout";
    }

    public static final class FILE{
        public static final String UPLOAD_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/upload";
        public static final String LIST_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/listUserFiles";
        public static final String DOWNLOAD_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/download";
        public static final String SHARE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/share";
        public static final String UNSHARE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/unshare";
        public static final String CHECK_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/check";
        public static final String GETACCESS_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/listAccessFileUser";
        public static final String UPDATE_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/update";
        public static final String INFO_FILE_SERVER_URL = SERVER_URL + FILE_SERVER_URL + "/fileInfo";
        public static final int IV_SIZE = 16;
        public static final int SALT_SIZE = 16;
        public static final int CIPHERED_HASH_SIZE = 256;
        public final static String SYMMETRIC_ALGORITHM = "AES";
        public final static String SYMMETRIC_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";
        public final static int SYMMETRIC_SIZE = 32; // This gives a key of 32 byte * 8 = 256 bits
    }


}
