package API.EventTom.services.vouchers;

import API.EventTom.repositories.VoucherRepository;
import API.EventTom.services.vouchers.interfaces.IVoucherCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AlphanumericIVoucherCodeGenerator implements IVoucherCodeGenerator {
    private final VoucherRepository voucherRepository;
    private static final int CODE_LENGTH = 8;

    @Override
    public String generateUniqueCode() {
        String code;
        do {
            code = RandomStringUtils.randomAlphanumeric(CODE_LENGTH).toUpperCase();
        } while (voucherRepository.existsByCode(code));
        return code;
    }
}

