package API.EventTom.exceptions.notFoundExceptions;


import jakarta.persistence.EntityNotFoundException;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}