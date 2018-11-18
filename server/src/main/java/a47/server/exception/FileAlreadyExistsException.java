package a47.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FileAlreadyExistsException extends ServerException{
    public FileAlreadyExistsException(int statusCode, String message) {
        super(statusCode, message);
    }
}