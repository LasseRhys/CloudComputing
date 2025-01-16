package API.EventTom.exceptions.tokenException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthenticatedException  extends IllegalStateException{
    public UserNotAuthenticatedException(String message) {
        super(message);
    }


}
