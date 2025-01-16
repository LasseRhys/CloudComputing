package API.EventTom.services.tickets.interfaces;

import API.EventTom.models.event.Event;

import java.math.BigDecimal;
import java.util.List;

public interface ITicketPriceCalculator {
    BigDecimal calculateBasePrice(Event event);
    BigDecimal calculateTotalPrice(Event event, int amount, List<String> voucherCodes);
}