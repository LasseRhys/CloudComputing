package API.EventTom.exceptions.notFoundExceptions;


public class CustomerNotFoundException extends ResourceNotFoundException {
    public CustomerNotFoundException(String customerNumber) {
        super("Customer not found with number: " + customerNumber);
    }
}