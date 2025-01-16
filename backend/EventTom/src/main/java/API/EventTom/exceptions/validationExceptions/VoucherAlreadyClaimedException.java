package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class VoucherAlreadyClaimedException extends ValidationException {
    public VoucherAlreadyClaimedException(String message) {
        super(message);
    }
}
