package API.EventTom.exceptions.notFoundExceptions;

public class VoucherNotFoundException extends ResourceNotFoundException {
    public VoucherNotFoundException(String code) {
        super("Voucher not found with code: " + code);
    }
}