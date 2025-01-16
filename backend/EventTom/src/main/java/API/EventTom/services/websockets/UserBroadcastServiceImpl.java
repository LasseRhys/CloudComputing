package API.EventTom.services.websockets;

import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.dto.WebSocketMessageDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.Notification;
import API.EventTom.models.user.UserDetailsImpl;
import API.EventTom.services.websockets.interfaces.IDestinationStrategy;
import API.EventTom.services.websockets.interfaces.IUserBroadcastService;
import API.EventTom.services.websockets.interfaces.IWebsocketBroadcastStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBroadcastServiceImpl implements IUserBroadcastService {
    private final IDestinationStrategy destinationStrategy;
    private final IWebsocketBroadcastStrategy broadcaster;
    private final StandardDTOMapper standardDTOMapper;
    @Override
    public void broadcastToUser(Long userId, Notification notification) {
        String destination = destinationStrategy.buildDestination("USER_NOTIFICATION", userId);
        broadcaster.broadcastToUser(userId.toString(), destination, standardDTOMapper.mapNotificationToNotificationDTO(notification));
    }
}
