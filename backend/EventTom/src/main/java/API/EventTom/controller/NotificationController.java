package API.EventTom.controller;

import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.dto.NotificationDTO;
import API.EventTom.dto.request.NotificationAsRead;
import API.EventTom.models.Notification;
import API.EventTom.services.notifications.INotificationQueryService;
import API.EventTom.services.notifications.IWebsiteNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final IWebsiteNotificationService notificationService;
    private final INotificationQueryService notificationQueryService;

    @GetMapping()
    public ResponseEntity<Page<NotificationDTO>> getAllNotifications(
            @AuthenticatedUserId Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "read") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(notificationQueryService.findAllByUserId(userId, pageable));
    }

    @GetMapping("/unread")

    public ResponseEntity<List<Notification>> getUnreadNotifications(@AuthenticatedUserId Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticatedUserId Long userId) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticatedUserId Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-multiple-read")
    public ResponseEntity<Void> markMultipleAsRead(@RequestBody NotificationAsRead notification, @AuthenticatedUserId Long userId) {
        notificationService.markMultipleAsRead(notification, userId);
        return ResponseEntity.ok().build();
    }
}