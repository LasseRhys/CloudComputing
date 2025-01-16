package API.EventTom.services.tickets.interfaces;

import API.EventTom.dto.request.PurchaseTicketDTO;
import API.EventTom.models.event.Event;

public interface ITicketValidator {
    void validatePurchaseRequest(Event event, PurchaseTicketDTO dto);
    void validateTicketAvailability(Event event, int requestedAmount);
    void validateEventDate(Event event);
}