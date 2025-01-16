package API.EventTom.services.vouchers.interfaces;

import API.EventTom.dto.request.BulkVoucherGenerationRequestDTO;
import API.EventTom.models.event.Voucher;
import API.EventTom.models.event.VoucherType;

import java.math.BigDecimal;
import java.util.List;

public interface IVoucherGenerationService {
    Voucher generateVoucher(BigDecimal amount, int validityDays, VoucherType type);
    List<Voucher> generateBulkVouchers(BulkVoucherGenerationRequestDTO bulkVoucherGenerationRequestDTO);
}