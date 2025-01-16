package API.EventTom.services.websockets.interfaces;


import API.EventTom.dto.WebSocketMessageDTO;
import API.EventTom.models.Notification;

public interface IUserBroadcastService {
    void broadcastToUser(Long userId, Notification notification);
}