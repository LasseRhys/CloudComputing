package API.EventTom.services.tickets;

import API.EventTom.dto.response.EventUpdateResponseDTO;
import API.EventTom.models.user.Customer;
import API.EventTom.models.event.Event;
import API.EventTom.models.event.Ticket;
import API.EventTom.observers.TicketPurchaseEvent;
import API.EventTom.repositories.EventRepository;
import API.EventTom.repositories.TicketRepository;
import API.EventTom.services.tickets.interfaces.ITicketCreationService;
import API.EventTom.services.tickets.interfaces.ITicketPriceCalculator;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketCreationServiceImpl implements ITicketCreationService {

    private final TicketRepository ticketRepository;

    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;
    private final ITicketPriceCalculator priceCalculator;

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    @Transactional
    public PurchaseResult processTicketPurchase(
            Event event,
            Customer customer,
            int amount,
            BigDecimal totalVoucherDiscount) {


        event = entityManager.merge(event);
        customer = entityManager.merge(customer);

        List<Long> ticketIds = new ArrayList<>();
        List<Ticket> createdTickets = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal remainingDiscount = totalVoucherDiscount;

        for (int i = 0; i < amount; i++) {
            entityManager.flush();
            entityManager.clear();
            event = entityManager.find(Event.class, event.getId());
            BigDecimal baseTicketPrice = priceCalculator.calculateBasePrice(event);

            TicketPriceResult priceResult = calculateTicketPriceWithDiscount(
                    baseTicketPrice,
                    remainingDiscount);

            Ticket ticket = createAndSaveTicket(event, customer, baseTicketPrice, priceResult.getFinalPrice());
            ticketIds.add(ticket.getId());
            createdTickets.add(ticket);
            totalPrice = totalPrice.add(priceResult.getFinalPrice());
            remainingDiscount = priceResult.getRemainingDiscount();
        }

        entityManager.flush();
        entityManager.clear();

        Event refreshedEvent = entityManager.find(Event.class, event.getId());
        createdTickets.forEach(ticket -> publishTicketPurchaseEvent(ticket, refreshedEvent));

        return new PurchaseResult(ticketIds, totalPrice);
    }

    private TicketPriceResult calculateTicketPriceWithDiscount(
            BigDecimal basePrice,
            BigDecimal remainingDiscount) {
        if (remainingDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TicketPriceResult(basePrice, BigDecimal.ZERO);
        }

        BigDecimal finalPrice = basePrice.subtract(remainingDiscount)
                .max(BigDecimal.ZERO)
                .setScale(SCALE, ROUNDING_MODE);

        BigDecimal newRemainingDiscount = finalPrice.compareTo(BigDecimal.ZERO) == 0
                ? remainingDiscount.subtract(basePrice)
                : BigDecimal.ZERO;

        return new TicketPriceResult(finalPrice, newRemainingDiscount);
    }

    private Ticket createAndSaveTicket(Event event, Customer customer, BigDecimal basePrice, BigDecimal finalPrice) {
        Ticket ticket = createTicket(event, customer, basePrice, finalPrice);
        return ticketRepository.save(ticket);
    }

    private Ticket createTicket(Event event, Customer customer, BigDecimal basePrice, BigDecimal finalPrice) {
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setCustomer(customer);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setStatusUsed(false);
        ticket.setBasePrice(basePrice);
        ticket.setFinalPrice(finalPrice);
        return ticket;
    }

    private void publishTicketPurchaseEvent(Ticket ticket, Event event) {
        eventPublisher.publishEvent(new TicketPurchaseEvent(this, ticket, event));
    }
}