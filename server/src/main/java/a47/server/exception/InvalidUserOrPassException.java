package a47.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidUserOrPassException extends ServerException {
    public InvalidUserOrPassException(int statusCode, String message) {
        super(statusCode, message);
    }
}
