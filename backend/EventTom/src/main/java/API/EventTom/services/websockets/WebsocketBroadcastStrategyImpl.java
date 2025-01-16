package API.EventTom.services.websockets;

import API.EventTom.services.websockets.interfaces.IWebsocketBroadcastStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketBroadcastStrategyImpl implements IWebsocketBroadcastStrategy {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcast(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    @Override
    public void broadcastToUser(String userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }
}
