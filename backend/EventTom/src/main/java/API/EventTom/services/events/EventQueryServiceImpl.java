package API.EventTom.services.events;

import API.EventTom.dto.EventDTO;
import API.EventTom.dto.response.EventUpdateResponseDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.event.Event;
import API.EventTom.models.user.Employee;
import API.EventTom.repositories.EventRepository;
import API.EventTom.services.common.BaseQueryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class EventQueryServiceImpl extends BaseQueryService<Event, EventDTO, Long>
        implements IEventQueryService {
    private final EventRepository eventRepository;
    private final StandardDTOMapper standardDTOMapper;

    public EventQueryServiceImpl(
            EventRepository eventRepository,
            StandardDTOMapper standardDTOMapper, EventRepository eventRepository1, StandardDTOMapper standardDTOMapper1) {
        super(eventRepository,
                standardDTOMapper,
                standardDTOMapper::mapEventToEventDTO,
                "Event");
        this.eventRepository = eventRepository1;
        this.standardDTOMapper = standardDTOMapper1;
    }

    @Override
    public Page<EventDTO> getAllEvents(
            int page, int size, String sortBy, String direction,
            String search) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Event> spec = Specification.where(null);

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                String searchLower = "%" + search.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("title")), searchLower),
                        cb.like(cb.lower(root.get("location")), searchLower)
                );
            });
        }

        Page<Event> events = eventRepository.findAll(spec, pageable);
        return events.map(mapperFunction);
    }

    @Override
    public EventUpdateResponseDTO getByIdWithManagerCheck(Long id, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));

        boolean isManager = event.getManagers().stream()
                .anyMatch(manager -> manager.getId().equals(userId));

        if (!isManager) {
            throw new AccessDeniedException("User does not have permission to access this event");
        }

        return standardDTOMapper.mapEventToEventUpdateDTO(event);
    }

    @Override
    public List<EventDTO> findAllByUserId(Long userId) {
        List<Event> events = eventRepository.findByManagers_Id(userId);
        return events.stream()
                .map(mapperFunction)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EventDTO> findAllByUserId(Long userId, Pageable pageable) {
        Page<Event> eventPage = eventRepository.findByManagers_Id(userId, pageable);
        return eventPage.map(mapperFunction);
    }
    @Override
    public Page<EventDTO> findAllByUserId(Long userId, Pageable pageable, String search) {
        Specification<Event> spec = (root, query, cb) -> {
            query.distinct(true);

            Join<Event, Employee> managersJoin = root.join("managers", JoinType.LEFT);

            return cb.or(
                    cb.equal(managersJoin.get("id"), userId)
            );
        };

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                String searchLower = "%" + search.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("title")), searchLower),
                        cb.like(cb.lower(root.get("location")), searchLower)
                );
            });
        }

        Page<Event> events = eventRepository.findAll(spec, pageable);
        return events.map(mapperFunction);
    }
}