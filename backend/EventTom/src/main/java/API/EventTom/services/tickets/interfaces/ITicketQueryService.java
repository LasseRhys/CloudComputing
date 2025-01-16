package API.EventTom.services.tickets.interfaces;

import API.EventTom.dto.TicketDTO;
import API.EventTom.services.common.IBaseQueryService;

import java.util.List;

public interface ITicketQueryService extends IBaseQueryService<TicketDTO, Long> {

    List<TicketDTO> getTicketsByEventId(Long id);
    List<TicketDTO> getTicketsByUserIdAndEventId(Long userId, Long eventId); // Add this method
    List<TicketDTO> getTicketsByCustomerNumber(String customerNumber);
}
