package API.EventTom.services.tickets.interfaces;

import API.EventTom.dto.request.PurchaseTicketDTO;
import API.EventTom.dto.response.TicketPurchaseResponseDTO;

import java.math.BigDecimal;

public interface ITicketPurchaseService {
    TicketPurchaseResponseDTO purchaseTicket(PurchaseTicketDTO purchaseTicketDTO, Long userId);
    BigDecimal calculateTotalPrice(PurchaseTicketDTO purchaseTicketDTO, Long userId);
}