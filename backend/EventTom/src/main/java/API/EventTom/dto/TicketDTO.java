package API.EventTom.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TicketDTO(
        BigDecimal finalPrice,
        LocalDateTime purchaseDate,
        boolean statusUsed,
        long eventId,
        long customerId,
        String eventTitle,
        LocalDateTime date,
        String location
) {}