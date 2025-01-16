package API.EventTom.dto.request;

import API.EventTom.models.event.VoucherType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherGenerationRequestDTO {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Validity days is required")
    @Positive(message = "Validity days must be positive")
    private Integer validityDays;

    @NotNull(message = "Voucher type is required")
    private VoucherType type;
}