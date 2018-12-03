package a47.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends ServerException {
    public AccessDeniedException(int statusCode, String message) {
        super(statusCode, message);
    }
}