package API.EventTom.dto.request;

import java.util.List;

public record NotificationAsRead(List<Long> notificationIds) {
}
