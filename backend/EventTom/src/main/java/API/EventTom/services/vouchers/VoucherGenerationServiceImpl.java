package API.EventTom.services.vouchers;

import API.EventTom.dto.request.BulkVoucherGenerationRequestDTO;
import API.EventTom.models.event.Voucher;
import API.EventTom.models.event.VoucherType;
import API.EventTom.repositories.VoucherRepository;
import API.EventTom.services.vouchers.interfaces.IVoucherCodeGenerator;
import API.EventTom.services.vouchers.interfaces.IVoucherGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
class VoucherGenerationServiceImpl implements IVoucherGenerationService {
    private final VoucherRepository voucherRepository;
    private final IVoucherCodeGenerator codeGenerator;

    @Override
    public Voucher generateVoucher(BigDecimal amount, int validityDays, VoucherType type) {
        Voucher voucher = new Voucher();
        voucher.setAmount(amount);
        voucher.setCode(codeGenerator.generateUniqueCode());
        voucher.setExpirationDate(LocalDateTime.now().plusDays(validityDays));
        voucher.setType(type);
        return voucherRepository.save(voucher);
    }

    @Override
    public List<Voucher> generateBulkVouchers(BulkVoucherGenerationRequestDTO bulkVoucherGenerationRequestDTO) {
        return IntStream.range(0, bulkVoucherGenerationRequestDTO.getCount())
                .mapToObj(i -> generateVoucher(bulkVoucherGenerationRequestDTO.getAmount(), bulkVoucherGenerationRequestDTO.getValidityDays(), bulkVoucherGenerationRequestDTO.getType()))
                .collect(Collectors.toList());
    }


}

