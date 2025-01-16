package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class ExpiredVoucherException extends ValidationException {
    public ExpiredVoucherException(String message) {
        super(message);
    }
}