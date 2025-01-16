package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class VoucherExpiredException extends ValidationException {
    public VoucherExpiredException(String message) {
        super(message);
    }
}