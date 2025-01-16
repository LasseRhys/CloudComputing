package API.EventTom.services.events;
import API.EventTom.dto.EventDTO;
import API.EventTom.dto.request.EventCreateDTO;
import API.EventTom.dto.request.EventUpdateDTO;
import API.EventTom.exceptions.notFoundExceptions.ResourceNotFoundException;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.user.Employee;
import API.EventTom.models.event.Event;
import API.EventTom.repositories.EmployeeRepository;
import API.EventTom.repositories.EventRepository;
import API.EventTom.services.websockets.interfaces.IEventBroadcastService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventCommandServiceImpl implements IEventCommandService {
    private final EventRepository eventRepository;
    private final EmployeeRepository employeeRepository;
    private final StandardDTOMapper standardDTOMapper;
    private final IEventBroadcastService eventBroadcastService;
    @Override
    @Transactional
    public EventDTO createEvent(EventCreateDTO eventCreateDTO, Long userId) {
        List<Employee> managers = eventCreateDTO.managerIds().stream()
                .map(id -> employeeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + id)))
                .collect(Collectors.toList());

        Employee creator = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

        Event event = new Event();
        event.setTitle(eventCreateDTO.title());
        event.setDateOfEvent(eventCreateDTO.dateOfEvent());
        event.setMaxTotalTickets(eventCreateDTO.totalTickets());
        event.setThresholdValue(eventCreateDTO.thresholdValue());
        event.setBasePrice(eventCreateDTO.basePrice());
        event.setManagers(managers);
        event.setCreator(creator);
        event.setLocation(eventCreateDTO.location());

        Event savedEvent = eventRepository.save(event);
        EventDTO eventDTO = standardDTOMapper.mapEventToEventDTO(savedEvent);

        eventBroadcastService.broadcastEventCreation(eventDTO);

        return eventDTO;
    }
    @Override
    @Transactional
    public EventDTO updateEvent(long id, EventUpdateDTO eventUpdateDTO, Long userId) throws AccessDeniedException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        boolean isAuthorized = event.getManagers().stream().anyMatch(manager -> manager.getId().equals(userId));


        if (!isAuthorized) {
            throw new AccessDeniedException("User is not authorized to update this event");
        }
        event.setTitle(eventUpdateDTO.title());
        event.setLocation(eventUpdateDTO.location());
        event.setDateOfEvent(eventUpdateDTO.dateOfEvent());
        event.setMaxTotalTickets(eventUpdateDTO.totalTickets());
        event.setThresholdValue(eventUpdateDTO.thresholdValue());
        event.setBasePrice(eventUpdateDTO.basePrice());

        if (eventUpdateDTO.managerIds() != null && !eventUpdateDTO.managerIds().isEmpty()) {
            List<Employee> newManagers = new ArrayList<>();
            for (Long managerId : eventUpdateDTO.managerIds()) {
                Employee manager = employeeRepository.findById(managerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + managerId));
                newManagers.add(manager);
            }
            event.getManagers().clear();
            event.getManagers().addAll(newManagers);
        }

        Event updatedEvent = eventRepository.save(event);
        eventBroadcastService.broadcastEventUpdate(updatedEvent, true);
        return standardDTOMapper.mapEventToEventDTO(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(long id, Long userId) throws AccessDeniedException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        boolean isAuthorized = event.getCreator().getId().equals(userId) ||
                event.getManagers().stream().anyMatch(manager -> manager.getId().equals(userId));

        if (!isAuthorized) {
            throw new AccessDeniedException("User is not authorized to delete this event");
        }

        eventRepository.deleteById(id);
    }
}