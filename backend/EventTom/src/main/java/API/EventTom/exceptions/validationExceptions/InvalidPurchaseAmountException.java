package API.EventTom.exceptions.validationExceptions;

import jakarta.validation.ValidationException;

public class InvalidPurchaseAmountException extends ValidationException {
    public InvalidPurchaseAmountException(String message) {
        super(message);
    }
}