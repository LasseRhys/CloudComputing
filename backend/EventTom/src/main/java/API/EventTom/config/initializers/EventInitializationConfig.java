package API.EventTom.config.initializers;

import API.EventTom.dto.request.EventCreateDTO;
import API.EventTom.services.events.EventCommandServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class EventInitializationConfig {

    private final EventCommandServiceImpl eventCommandService;

    @Bean
    @Order(2)
    public CommandLineRunner initializeEvents() {
        return args -> {
            createSampleEvent(
                    "Summer Music Festival",
                    "Central Park",
                    LocalDateTime.now().plusMonths(2),
                    1000,
                    100,
                    new BigDecimal("49.99"),
                    Arrays.asList(1L, 2L)
            );

            createSampleEvent(
                    "Tech Conference 2025",
                    "Convention Center",
                    LocalDateTime.now().plusMonths(3),
                    500,
                    50,
                    new BigDecimal("199.99"),
                    Arrays.asList(2L, 3L)
            );

            createSampleEvent(
                    "Food & Wine Expo",
                    "City Hall",
                    LocalDateTime.now().plusMonths(1),
                    300,
                    30,
                    new BigDecimal("79.99"),
                    Arrays.asList(1L, 3L)
            );
        };
    }

    private void createSampleEvent(
            String title,
            String location,
            LocalDateTime dateOfEvent,
            int totalTickets,
            int thresholdValue,
            BigDecimal basePrice,
            List<Long> managerIds
    ) {
        EventCreateDTO eventCreateDTO = new EventCreateDTO(title, location, dateOfEvent, totalTickets, thresholdValue, basePrice, managerIds);

        try {
            eventCommandService.createEvent(eventCreateDTO, 1L);
        } catch (Exception e) {
            System.err.println("Failed to create sample event: " + title);
            e.printStackTrace();
        }
    }
}