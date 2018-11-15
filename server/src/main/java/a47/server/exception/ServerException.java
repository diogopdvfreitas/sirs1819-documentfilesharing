package a47.server.exception;

public class ServerException extends RuntimeException{

    private int statusCode = ErrorMessage.CODE_SERVER_GENERAL;

    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    protected ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
