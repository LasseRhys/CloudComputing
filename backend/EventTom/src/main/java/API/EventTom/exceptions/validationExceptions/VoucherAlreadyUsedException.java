package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class VoucherAlreadyUsedException extends ValidationException {
    public VoucherAlreadyUsedException(String message) {
        super(message);
    }
}

