package API.EventTom.services.notifications;

import API.EventTom.dto.NotificationDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.Notification;
import API.EventTom.repositories.NotificationRepository;
import API.EventTom.services.common.BaseQueryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class NotificationQueryServiceImpl extends BaseQueryService<Notification, NotificationDTO, Long> implements INotificationQueryService {

    private final NotificationRepository notificationRepository;

    protected NotificationQueryServiceImpl(NotificationRepository repository, StandardDTOMapper standardDTOMapper, NotificationRepository notificationRepository) {
        super(repository, standardDTOMapper, standardDTOMapper::mapNotificationToNotificationDTO, "Notification");
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<NotificationDTO> findAllByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        return notifications.stream()
                .map(mapperFunction)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationDTO> findAllByUserId(Long userId, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findAllByUserId(userId, pageable);
        return notificationPage.map(mapperFunction);
    }

}
