package API.EventTom.services.vouchers.interfaces;

import API.EventTom.models.event.Voucher;
import API.EventTom.models.user.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface IVoucherUsageService {
    void useVoucherForPurchase(String code, Customer customer);
    BigDecimal calculateDiscountedAmount(BigDecimal originalAmount, Voucher voucher);
    Voucher validateVoucher(String code, Long userId);
    Voucher validateVoucher(String code);
    List<Voucher> validateVouchers(List<String> code);
    BigDecimal calculateTotalDiscount(List<Voucher> vouchers);
    void markVouchersAsUsed(List<Voucher> vouchers, Customer customer);
}