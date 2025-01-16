package API.EventTom.services.websockets.interfaces;

import API.EventTom.dto.EventDTO;
import API.EventTom.models.event.Event;

public interface IEventBroadcastService {
    void broadcastEventCreation(EventDTO eventDTO);
    void broadcastEventUpdate(Event event, boolean notifyAllUsers);
}