package API.EventTom.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventCreateDTO(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Location is required")
        String location,

        @NotNull(message = "Event date is required")
        @Future(message = "Event date must be in the future")
        LocalDateTime dateOfEvent,

        @Positive(message = "Total tickets must be positive")
        @Max(value = 100000, message = "Total tickets cannot exceed 100,000")
        int totalTickets,

        @PositiveOrZero(message = "Threshold value must be zero or positive")
        int thresholdValue,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be positive")
        BigDecimal basePrice,

        // @NotEmpty(message = "At least one manager must be assigned")
        List<Long> managerIds
) {}

