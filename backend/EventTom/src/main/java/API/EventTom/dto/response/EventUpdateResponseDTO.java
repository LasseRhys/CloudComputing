package API.EventTom.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventUpdateResponseDTO(
        Long id,
        String title,
        LocalDateTime dateOfEvent,
        long soldTickets,
        long thresholdValue,
        int availableTickets,
        BigDecimal price,
        BigDecimal basePrice,
        String location,
        int totalTickets,
        List<Long> managerIds
){
}
