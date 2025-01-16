package API.EventTom.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherDTO(
        long customerId,
        BigDecimal amount,
        LocalDateTime voucherValidUntil,
        boolean used,
        String voucherCode,
        Long voucherId
) {}