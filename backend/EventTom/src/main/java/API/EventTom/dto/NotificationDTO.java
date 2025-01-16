package API.EventTom.dto;

import java.time.LocalDateTime;

public record NotificationDTO(Long id, Long userId, LocalDateTime createdAt, String message, String type, boolean isRead) {
}
