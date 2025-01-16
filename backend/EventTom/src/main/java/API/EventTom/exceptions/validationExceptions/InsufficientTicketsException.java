package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class InsufficientTicketsException extends ValidationException {
    public InsufficientTicketsException(String message) {
        super(message);
    }
}