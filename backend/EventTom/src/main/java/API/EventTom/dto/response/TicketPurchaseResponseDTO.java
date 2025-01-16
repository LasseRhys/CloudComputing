package API.EventTom.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TicketPurchaseResponseDTO(
        String eventTitle,
        LocalDateTime eventDate,
        int purchasedTickets,
        BigDecimal totalPrice,
        BigDecimal pricePerTicket,
        List<Long> ticketIds,
        String location
) {}
