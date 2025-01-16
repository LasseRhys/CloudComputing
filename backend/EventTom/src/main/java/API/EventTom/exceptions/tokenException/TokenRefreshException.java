package API.EventTom.exceptions.tokenException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

public class TokenRefreshException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("[%s]: %s", token, message));
    }
}