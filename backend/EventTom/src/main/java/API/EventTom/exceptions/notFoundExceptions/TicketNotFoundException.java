package API.EventTom.exceptions.notFoundExceptions;

public class TicketNotFoundException extends ResourceNotFoundException {
    public TicketNotFoundException(Long ticketId) {
        super("Ticket not found with ID: " + ticketId);
    }
}