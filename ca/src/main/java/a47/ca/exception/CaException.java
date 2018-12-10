package a47.ca.exception;

public class CaException extends RuntimeException{

    private int statusCode = ErrorMessage.CODE_SERVER_GENERAL;

    public CaException() {
        super();
    }

    public CaException(String message) {
        super(message);
    }

    public CaException(int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }

    public CaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaException(Throwable cause) {
        super(cause);
    }

    protected CaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
