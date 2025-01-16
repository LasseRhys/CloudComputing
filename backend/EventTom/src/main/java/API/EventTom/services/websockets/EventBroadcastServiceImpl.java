package API.EventTom.services.websockets;

import API.EventTom.dto.EventDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.event.Event;
import API.EventTom.services.websockets.interfaces.IDestinationStrategy;
import API.EventTom.services.websockets.interfaces.IEventBroadcastService;
import API.EventTom.services.websockets.interfaces.IWebsocketBroadcastStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EventBroadcastServiceImpl implements IEventBroadcastService {
    private final StandardDTOMapper standardDTOMapper;
    private final IDestinationStrategy destinationStrategy;
    private final IWebsocketBroadcastStrategy broadcaster;

    public void broadcastEventCreation(EventDTO event) {
        String destination = destinationStrategy.buildDestination("NEW_EVENT");
        broadcaster.broadcast(destination, event);
    }

    @Override
    public void broadcastEventUpdate(Event event, boolean notifyAllUsers) {
        EventDTO eventDTO = standardDTOMapper.mapEventToEventDTO(event);

        if (notifyAllUsers) {
            String destination = destinationStrategy.buildDestination("EVENT_UPDATE", event.getId());
            broadcaster.broadcast(destination, eventDTO);
        }

        event.getManagers().forEach(manager -> {
                String destination = destinationStrategy.buildDestination("MANAGER_EVENT", manager.getId(), event.getId());
                broadcaster.broadcast(destination, eventDTO);
        });
    }
}
