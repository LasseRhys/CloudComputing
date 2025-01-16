package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class InvalidVoucherException extends ValidationException {
    public InvalidVoucherException(String message) {
        super(message);
    }
}