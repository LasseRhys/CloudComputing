package API.EventTom.exceptions.notFoundExceptions;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}