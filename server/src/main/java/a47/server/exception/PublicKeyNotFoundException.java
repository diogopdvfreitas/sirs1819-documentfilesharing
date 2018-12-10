package a47.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PublicKeyNotFoundException extends ServerException{
    public PublicKeyNotFoundException(int statusCode, String message) {
        super(statusCode, message);
    }
}
