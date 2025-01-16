package API.EventTom.observers;

import API.EventTom.dto.WebSocketMessageDTO;
import API.EventTom.models.Notification;
import API.EventTom.models.user.Roles;
import API.EventTom.models.user.User;
import API.EventTom.repositories.EmployeeRepository;
import API.EventTom.services.notifications.IWebsiteNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketNotificationListener {
    private final EmployeeRepository employeeRepository;
    private final IWebsiteNotificationService websiteNotificationService;

    @Async
    @EventListener
    @Transactional
    public void handleTicketPurchase(TicketPurchaseEvent event) {
        sendCustomerNotification(event, "TICKET_PURCHASE");

        if (shouldNotifyEventManager(event)) {
            sendManagerNotification(event, "EVENT_MANAGER_TICKET_PURCHASE");
        }
    }

    private void sendCustomerNotification(TicketPurchaseEvent event, String notificationType) {
        String message = createCustomerMessage(event);
        User recipient = event.getTicket().getCustomer().getUser();
        websiteNotificationService.sendNotification(recipient, message, notificationType);

    }

    private void sendManagerNotification(TicketPurchaseEvent event, String notificationType) {
        String message = createManagerMessage(event);

        employeeRepository.findAllByEvent(event.getEvent())
                .forEach(manager -> {
                    User userManager = manager.getUser();
                    websiteNotificationService.sendNotification(userManager, message, notificationType);
                });
    }

    private boolean shouldNotifyEventManager(TicketPurchaseEvent event) {
        return true;
        /*
        return event.getEvent().isThresholdReached() ||
                event.getSoldPercentage() >= 90.0 ||
                event.getRemainingTickets() <= 10;
        */
    }


    private String createManagerMessage(TicketPurchaseEvent event) {
        return String.format("Event: %s\nSold: %d/%d (%.1f%%)\nRemaining: %d",
                event.getEvent().getTitle(),
                event.getEvent().getTotalSoldTickets(),
                event.getEvent().getMaxTotalTickets(),
                event.getSoldPercentage(),
                event.getRemainingTickets());
    }

    private String createCustomerMessage(TicketPurchaseEvent event) {
        return String.format(
                "Thank you for your purchase!\n\n" +
                        "Event: %s\n" +
                        "Date: %s\n" +
                        "Purchase Time: %s\n" +
                        "Price: %.2f €\n\n" +
                        "Please keep this confirmation for your records.",
                event.getEvent().getTitle(),
                event.getEvent().getDateOfEvent(),
                event.getTicket().getPurchaseDate(),
                event.getTicket().getFinalPrice()
        );
    }
}