package API.EventTom.controller.tickets;

import API.EventTom.dto.request.PurchaseTicketDTO;
import API.EventTom.dto.response.TicketPurchaseResponseDTO;
import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.services.tickets.interfaces.ITicketPurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("api/tickets/command")
@RequiredArgsConstructor
public class TicketCommandController {
    private final ITicketPurchaseService ticketPurchaseService;

    @PostMapping("/calculate-price")
    public ResponseEntity<BigDecimal> calculatePrice(@Valid @RequestBody PurchaseTicketDTO purchaseTicketDTO, @AuthenticatedUserId Long userId) {
        BigDecimal totalPrice = ticketPurchaseService.calculateTotalPrice(purchaseTicketDTO, userId);
        return ResponseEntity.ok(totalPrice);
    }

    @PostMapping("/purchase")
    public ResponseEntity<TicketPurchaseResponseDTO> purchaseTicket(
            @Valid @RequestBody PurchaseTicketDTO purchaseTicketDTO,
            @AuthenticatedUserId Long userId) {
        TicketPurchaseResponseDTO response = ticketPurchaseService.purchaseTicket(purchaseTicketDTO, userId);
        return ResponseEntity.ok(response);
    }
}