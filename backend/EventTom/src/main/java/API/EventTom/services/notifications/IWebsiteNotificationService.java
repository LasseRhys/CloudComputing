package API.EventTom.services.notifications;

import API.EventTom.dto.request.NotificationAsRead;
import API.EventTom.models.Notification;
import java.util.List;

public interface IWebsiteNotificationService extends INotificationService {
    List<Notification> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    void markMultipleAsRead(NotificationAsRead notificationAsReads, Long userId);
}