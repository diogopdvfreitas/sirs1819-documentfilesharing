package a47.client;

public class Constants {
    public static final class Challenge{
        public static final int SIZE = 128;
        public static final long TIMEOUT = 5000; //ms
    }

    public static final class Keys{
        public static final String CA_KEYSTORE_CIPHER = "RSA";
    }

    public static final class CA{
        public static final String URL = "http://localhost:30000/";
        public static final String PUBLISH_URL = URL + "publish";
    }
}
