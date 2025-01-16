package API.EventTom.services.tickets;

import API.EventTom.dto.TicketDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.event.Event;
import API.EventTom.models.event.Ticket;
import API.EventTom.repositories.CustomerRepository;
import API.EventTom.repositories.EventRepository;
import API.EventTom.repositories.TicketRepository;
import API.EventTom.services.common.BaseQueryService;
import API.EventTom.services.tickets.interfaces.ITicketQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TicketQueryServiceImpl extends BaseQueryService<Ticket, TicketDTO, Long> implements ITicketQueryService {

    private final TicketRepository ticketRepository;
    private final StandardDTOMapper standardDTOMapper;


    public TicketQueryServiceImpl(
            TicketRepository ticketRepository,
            StandardDTOMapper standardDTOMapper) {
        super(ticketRepository,
                standardDTOMapper,
                standardDTOMapper::mapTicketToTicketDTO,
                "Ticket");
        this.ticketRepository = ticketRepository;
        this.standardDTOMapper = standardDTOMapper;

    }

    @Override
    public List<TicketDTO> getTicketsByEventId(Long id) {
        List<Ticket> tickets = ticketRepository.findAllByEventId(id);
        return tickets.stream()
                .map(standardDTOMapper::mapTicketToTicketDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getTicketsByCustomerNumber(String customerNumber) {
        List<Ticket> tickets = ticketRepository.findAllTicketsByCustomerNumber(customerNumber);
        return tickets.stream()
                .map(standardDTOMapper::mapTicketToTicketDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> getTicketsByUserIdAndEventId(Long userId, Long eventId) {
        List<Ticket> tickets = ticketRepository.findAllByUserIdAndEventId(userId, eventId);
        return tickets.stream()
                .map(standardDTOMapper::mapTicketToTicketDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> findAllByUserId(Long userId) {
        List<Ticket> tickets = ticketRepository.findAllByUserId(userId);
        return tickets.stream()
                .map(standardDTOMapper::mapTicketToTicketDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TicketDTO> findAllByUserId(Long userId, Pageable pageable) {
        Page<Ticket> ticketPage = ticketRepository.findAllByUserId(userId, pageable);
        return ticketPage.map(standardDTOMapper::mapTicketToTicketDTO);
    }

}
