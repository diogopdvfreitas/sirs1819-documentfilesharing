package a47.client;

public class Constants {

    public static final String CA_URL = "http://localhost:30000";
    public static final String SERVER_URL = "http://localhost:30001";

    public static final class Keys{
        public static final String CA_KEYSTORE_CIPHER = "RSA";
    }

    public static final class CA{
        public static final String PUBLISH_URL = CA_URL + "/publish";
        public static final String PUBLISH_RESPONSE_URL = CA_URL + "/publish/response";
    }

    public static final class SERVER{
        public static final String REGISTER_SERVER_URL = SERVER_URL + "/auth/register";
        public static final String LOGIN_SERVER_URL = SERVER_URL + "/auth/login";
        public static final String LOGOUT_SERVER_URL = SERVER_URL + "/auth/logout";
    }

    public static final class FILE{
        public static final String UPLOAD_FILE_SERVER_URL = SERVER_URL + "/files/upload";
        public final static String SYMMETRIC_ALGORITHM = "AES";
        public final static String SYMMETRIC_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";
        public final static int SYMMETRIC_SIZE = 32; // This gives a key of 32 byte * 8 = 256 bits
    }

}
