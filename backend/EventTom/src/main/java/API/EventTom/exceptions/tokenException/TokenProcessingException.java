package API.EventTom.exceptions.tokenException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TokenProcessingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TokenProcessingException(String message) {
        super(String.format("Failed for %s", message));
    }
}