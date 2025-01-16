package API.EventTom.dto.response;

import java.time.LocalDateTime;

public record NextEventDTO(String title, LocalDateTime dateOfEvent, String location) {
}