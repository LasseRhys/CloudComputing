package API.EventTom.services.tickets;

import API.EventTom.dto.request.PurchaseTicketDTO;
import API.EventTom.dto.response.TicketPurchaseResponseDTO;
import API.EventTom.exceptions.notFoundExceptions.CustomerNotFoundException;
import API.EventTom.exceptions.notFoundExceptions.EventNotFoundException;
import API.EventTom.models.user.Customer;
import API.EventTom.models.event.Event;
import API.EventTom.models.event.Voucher;
import API.EventTom.repositories.CustomerRepository;
import API.EventTom.repositories.EventRepository;
import API.EventTom.services.tickets.interfaces.ITicketCreationService;
import API.EventTom.services.tickets.interfaces.ITicketPriceCalculator;
import API.EventTom.services.tickets.interfaces.ITicketPurchaseService;
import API.EventTom.services.tickets.interfaces.ITicketValidator;
import API.EventTom.services.vouchers.interfaces.IVoucherUsageService;
import API.EventTom.services.websockets.interfaces.IEventBroadcastService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class TicketPurchaseServiceImpl implements ITicketPurchaseService {
    private final EventRepository eventRepository;
    private final CustomerRepository customerRepository;
    private final IVoucherUsageService voucherUsageService;
    private final ITicketPriceCalculator priceCalculator;
    private final ITicketValidator ticketValidator;
    private final ITicketCreationService ticketCreationService;
    private final IEventBroadcastService eventBroadcastService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BigDecimal calculateTotalPrice(PurchaseTicketDTO purchaseTicketDTO, Long userId) {
        Event event = findEvent(purchaseTicketDTO.eventId());
        ticketValidator.validatePurchaseRequest(event, purchaseTicketDTO);

        return priceCalculator.calculateTotalPrice(
                event,
                purchaseTicketDTO.amount(),
                purchaseTicketDTO.voucherCodes()
        );
    }

    @Override
    @Transactional
    public TicketPurchaseResponseDTO purchaseTicket(PurchaseTicketDTO purchaseTicketDTO, Long userId) {
        Event event = findEvent(purchaseTicketDTO.eventId());
        Customer customer = findCustomer(userId);

        ticketValidator.validatePurchaseRequest(event, purchaseTicketDTO);

        List<Voucher> validatedVouchers = voucherUsageService.validateVouchers(purchaseTicketDTO.voucherCodes());
        BigDecimal totalVoucherDiscount = voucherUsageService.calculateTotalDiscount(validatedVouchers);
        voucherUsageService.markVouchersAsUsed(validatedVouchers, customer);

        PurchaseResult purchaseResult = ticketCreationService.processTicketPurchase(
                event, customer, purchaseTicketDTO.amount(),
                totalVoucherDiscount
        );

        eventRepository.save(event);
        entityManager.flush();
        entityManager.clear();
        event = eventRepository.findById(event.getId()).orElseThrow();
        BigDecimal baseTicketPrice = event.getBasePrice();
        eventBroadcastService.broadcastEventUpdate(event, true);


        return createPurchaseResponse(event, purchaseResult, baseTicketPrice);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Customer findCustomer(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for user ID: " + userId));
    }

    private TicketPurchaseResponseDTO createPurchaseResponse(
            Event event, PurchaseResult result, BigDecimal baseTicketPrice) {
        return new TicketPurchaseResponseDTO(
                event.getTitle(),
                event.getDateOfEvent(),
                result.ticketIds().size(),
                result.totalPrice(),
                baseTicketPrice,
                result.ticketIds(),
                event.getLocation()
        );
    }

}