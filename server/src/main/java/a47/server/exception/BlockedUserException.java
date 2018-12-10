package a47.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BlockedUserException extends ServerException {
    public BlockedUserException(int statusCode, String message) {
        super(statusCode, message);
    }
}