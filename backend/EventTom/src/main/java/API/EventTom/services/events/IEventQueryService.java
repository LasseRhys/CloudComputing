package API.EventTom.services.events;


import API.EventTom.dto.EventDTO;
import API.EventTom.dto.response.EventUpdateResponseDTO;
import API.EventTom.services.common.IBaseQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEventQueryService extends IBaseQueryService<EventDTO, Long> {
    Page<EventDTO> getAllEvents(
            int page, int size, String sortBy, String direction,
            String search);
    EventUpdateResponseDTO getByIdWithManagerCheck(Long id, Long userId);
    Page<EventDTO> findAllByUserId(Long userId, Pageable pageable, String search);
}
