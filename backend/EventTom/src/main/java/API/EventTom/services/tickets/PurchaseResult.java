package API.EventTom.services.tickets;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseResult(List<Long> ticketIds, BigDecimal totalPrice) { }

