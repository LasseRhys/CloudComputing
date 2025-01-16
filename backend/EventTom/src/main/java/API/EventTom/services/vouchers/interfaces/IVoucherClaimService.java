package API.EventTom.services.vouchers.interfaces;

import API.EventTom.models.event.Voucher;

public interface IVoucherClaimService {
    Voucher claimVoucher(String code, Long customerId);
    boolean isVoucherClaimable(String code);
}