package API.EventTom.exceptions.notFoundExceptions;


public class EventNotFoundException extends ResourceNotFoundException {
    public EventNotFoundException(Long eventId) {
        super("Event not found with ID: " + eventId);
    }
}
