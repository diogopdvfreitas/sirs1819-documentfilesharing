package a47.server.exception;

import java.util.Date;

public class ErrorMessage {
    public static final int CODE_SERVER_GENERAL = 10;
    public static final int CODE_SERVER_DUP_USER = 11;
    public static final int CODE_SERVER_INV_USER = 12;
    public static final int CODE_SERVER_USER_NOT_FOUND = 13;
    public static final int CODE_SERVER_ACCESS_DENIED = 14;
    public static final int CODE_SERVER_USER_BLOCKED = 15;
    public static final int CODE_SERVER_DUP_REPLICA = 16;
    public static final int CODE_SERVER_DUP_FILE = 21;
    public static final int CODE_SERVER_NOT_FOUND_FILE = 22;

    private int statusCode;
    private Date timestamp;
    private String message;
    private String url;

    public ErrorMessage(int statusCode, Date timestamp, String message, String url) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
}
