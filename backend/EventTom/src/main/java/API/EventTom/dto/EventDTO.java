package API.EventTom.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDTO(
        Long id,
        String title,
        LocalDateTime dateOfEvent,
        long soldTickets,
        long thresholdValue,
        int availableTickets,
        BigDecimal price,
        BigDecimal basePrice,
        String location,
        int totalTickets
) {}

