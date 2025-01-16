package API.EventTom.mappers;

import API.EventTom.dto.*;
import API.EventTom.dto.response.EventUpdateResponseDTO;
import API.EventTom.dto.response.UserDTO;
import API.EventTom.models.Notification;
import API.EventTom.models.event.Event;
import API.EventTom.models.event.Ticket;
import API.EventTom.models.event.Voucher;
import API.EventTom.models.user.Customer;
import API.EventTom.models.user.Employee;
import API.EventTom.models.user.Role;
import API.EventTom.models.user.User;
import API.EventTom.services.tickets.interfaces.ITicketPriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StandardDTOMapper {
    private final ITicketPriceCalculator priceCalculator;

    public CustomerDTO mapCustomerToCustomerDTO(Customer customer) {
        User user = customer.getUser();
        return new CustomerDTO(
                new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()),
                customer.getCustomerNumber()
        );
    }

    public EmployeeDTO mapEmployeeToEmployeeDTO(Employee employee) {
        User user = employee.getUser();
        return new EmployeeDTO(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                employee.getEmployeeNumber(),
                new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail())
        );
    }

    public RoleDTO mapRoleToRoleDTO(Role role) {
        return new RoleDTO(role.getId(), role.getName());
    }

    public EventDTO mapEventToEventDTO(Event event) {
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDateOfEvent(),
                event.getTotalSoldTickets(),
                event.getThresholdValue(),
                event.getAvailableTickets(),
                priceCalculator.calculateBasePrice(event),
                event.getBasePrice(),
                event.getLocation(),
                event.getMaxTotalTickets()
        );
    }

    public EventUpdateResponseDTO mapEventToEventUpdateDTO(Event event) {
        return new EventUpdateResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDateOfEvent(),
                event.getTotalSoldTickets(),
                event.getThresholdValue(),
                event.getAvailableTickets(),
                priceCalculator.calculateBasePrice(event),
                event.getBasePrice(),
                event.getLocation(),
                event.getMaxTotalTickets(),
                event.getManagers().stream()
                        .map(Employee::getId)
                        .collect(Collectors.toList())
        );
    }
    public VoucherDTO mapVoucherToVoucherDTO(Voucher voucher) {
        long userId = -1L;
        if (voucher.getCustomer() != null && voucher.getCustomer().getUser() != null) {
            userId = voucher.getCustomer().getUser().getId();
        }

        return new VoucherDTO(
                userId,
                voucher.getAmount(),
                voucher.getExpirationDate(),
                voucher.isUsed(),
                voucher.getCode(),
                voucher.getId()
        );
    }

    public TicketDTO mapTicketToTicketDTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getFinalPrice(),
                ticket.getPurchaseDate(),
                ticket.isStatusUsed(),
                ticket.getEvent().getId(),
                ticket.getCustomer().getUser().getId(),
                ticket.getEvent().getTitle(),
                ticket.getEvent().getDateOfEvent(),
                ticket.getEvent().getLocation()
        );
    }
    public NotificationDTO mapNotificationToNotificationDTO(Notification notification) {
        return new NotificationDTO(notification.getId(), notification.getUser().getId(), notification.getCreatedAt(), notification.getMessage(), notification.getNotificationType(), notification.isRead());
    }

    public UserDTO mapUserToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

}