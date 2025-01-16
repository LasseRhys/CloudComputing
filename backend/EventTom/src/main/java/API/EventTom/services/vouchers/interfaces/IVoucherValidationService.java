package API.EventTom.services.vouchers.interfaces;
import API.EventTom.models.event.Voucher;


public interface IVoucherValidationService {
    Voucher validateVoucherExists(String code);
    void validateVoucherNotExpired(Voucher voucher);
    void validateVoucherNotUsed(Voucher voucher);
    void validateVoucherNotClaimed(Voucher voucher);
    void validateVoucherOwnership(Voucher voucher, Long customerId);
}
