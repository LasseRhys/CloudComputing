package API.EventTom.services.tickets.interfaces;

import API.EventTom.models.user.Customer;
import API.EventTom.models.event.Event;
import API.EventTom.services.tickets.PurchaseResult;

import java.math.BigDecimal;

public interface ITicketCreationService {
    PurchaseResult processTicketPurchase(Event event, Customer customer,
                                         int amount,
                                         BigDecimal totalVoucherDiscount);
}
