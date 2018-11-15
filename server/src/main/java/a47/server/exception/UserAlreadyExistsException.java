package a47.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends ServerException{
    public UserAlreadyExistsException(int statusCode, String message) {
        super(statusCode, message);
    }
}
