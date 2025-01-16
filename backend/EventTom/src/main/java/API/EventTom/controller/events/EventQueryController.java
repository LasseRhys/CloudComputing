package API.EventTom.controller.events;

import API.EventTom.dto.EventDTO;
import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.dto.response.EventUpdateResponseDTO;
import API.EventTom.services.events.IEventQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/events/query")
public class EventQueryController {
    private final IEventQueryService eventQueryService;

    @GetMapping
    public ResponseEntity<Page<EventDTO>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search


    ) {
        return ResponseEntity.ok(eventQueryService.getAllEvents(
                page, size, sortBy, direction,
                search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable long id) {
        return ResponseEntity.ok(eventQueryService.getById(id));
    }

    @GetMapping("/managed/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'EVENT_MANAGER')")
    public ResponseEntity<EventUpdateResponseDTO> getManagedEventById(@PathVariable long id, @AuthenticatedUserId Long userId) {
        return ResponseEntity.ok(eventQueryService.getByIdWithManagerCheck(id, userId));
    }

    @GetMapping("/manager")
    public ResponseEntity<Page<EventDTO>> getEventsByManager(
            @AuthenticatedUserId Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(eventQueryService.findAllByUserId(userId, pageable, search));
    }
}
