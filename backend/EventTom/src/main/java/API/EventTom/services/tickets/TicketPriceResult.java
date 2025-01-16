package API.EventTom.services.tickets;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class TicketPriceResult {
    BigDecimal finalPrice;
    BigDecimal remainingDiscount;
}
