package API.EventTom.services.notifications;

import API.EventTom.dto.request.NotificationAsRead;
import API.EventTom.models.Notification;
import API.EventTom.models.user.User;
import API.EventTom.repositories.NotificationRepository;
import API.EventTom.repositories.UserRepository;
import API.EventTom.services.websockets.interfaces.IUserBroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebsiteNotificationServiceImpl implements IWebsiteNotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final IUserBroadcastService userBroadcastService;

    @Override
    @Transactional
    public void sendNotification(User recipient, String message, String notificationType) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setNotificationType(notificationType);
        userBroadcastService.broadcastToUser(recipient.getId(), notification);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserAndReadOrderByCreatedAtDesc(user, false);
    }


    @Transactional
    @Override
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    if (!notification.getUser().getId().equals(userId)) {
                        throw new AccessDeniedException("User " + userId + " is not authorized to mark this notification as read");
                    }

                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }

    @Transactional
    @Override
    public void markMultipleAsRead(NotificationAsRead notificationAsReads, Long userId) {
        List<Notification> notifications = notificationRepository.findAllById(notificationAsReads.notificationIds());

        for (Notification notification : notifications) {
            if (!notification.getUser().getId().equals(userId)) {
                throw new AccessDeniedException("User " + userId +
                        " is not authorized to mark notification " + notification.getId() + " as read");
            }
        }

        notifications.forEach(notification -> {
            notification.setRead(true);
        });

        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        notificationRepository.markAllAsRead(user);
    }


}