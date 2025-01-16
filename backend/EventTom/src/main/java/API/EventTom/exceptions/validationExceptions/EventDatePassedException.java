package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class EventDatePassedException extends ValidationException {
    public EventDatePassedException(String message) {
        super(message);
    }
}