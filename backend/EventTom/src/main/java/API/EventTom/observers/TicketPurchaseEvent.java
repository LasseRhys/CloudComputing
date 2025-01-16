package API.EventTom.observers;

import API.EventTom.models.event.Event;
import API.EventTom.models.event.Ticket;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class TicketPurchaseEvent extends ApplicationEvent {
    private final Ticket ticket;
    private final Event event;
    private final int remainingTickets;
    private final double soldPercentage;

    public TicketPurchaseEvent(Object source, Ticket ticket, Event event) {
        super(source);
        this.ticket = ticket;
        this.event = event;
        this.remainingTickets = event.getAvailableTickets();
        this.soldPercentage = (double) (event.getTotalSoldTickets()) / event.getMaxTotalTickets() * 100;
    }
}