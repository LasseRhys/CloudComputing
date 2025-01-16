package API.EventTom.exceptions.userExceptions;

public class UnauthorizedVoucherUseException extends RuntimeException {
    public UnauthorizedVoucherUseException(String message) {
        super(message);
    }
}
