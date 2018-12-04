package a47.client;

public class Constants {

    public static final String CA_URL = "http://localhost:30000";
    public static final String SERVER_URL = "http://localhost:30001";

    public static final class Challenge{
        public static final int SIZE = 128;
        public static final long TIMEOUT = 5000; //ms
    }

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
}
