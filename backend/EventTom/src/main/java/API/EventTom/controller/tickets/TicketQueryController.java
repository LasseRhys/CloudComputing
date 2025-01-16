package API.EventTom.controller.tickets;

import API.EventTom.dto.TicketDTO;
import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.services.tickets.interfaces.ITicketQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/tickets/query")
@RequiredArgsConstructor
public class TicketQueryController {
    private final ITicketQueryService ticketQueryService;

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketDTO>> getEventTickets(@PathVariable Long eventId) {
        List<TicketDTO> tickets = ticketQueryService.getTicketsByEventId(eventId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<TicketDTO>> getLoggedInUserTickets(@AuthenticatedUserId Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TicketDTO> tickets = ticketQueryService.findAllByUserId(userId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/event/{eventId}/user")
    public ResponseEntity<List<TicketDTO>> getLoggedInUserTicketsByEvent(
            @AuthenticatedUserId Long userId,
            @PathVariable Long eventId) {
        List<TicketDTO> tickets = ticketQueryService.getTicketsByUserIdAndEventId(userId, eventId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<TicketDTO>> getCustomerTickets(@PathVariable String customerNumber) {
        List<TicketDTO> tickets = ticketQueryService.getTicketsByCustomerNumber(customerNumber);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable long id) {
        TicketDTO ticketDTO = ticketQueryService.getById(id);
        return ResponseEntity.ok(ticketDTO);
    }
}