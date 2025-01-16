package API.EventTom.repositories;

import API.EventTom.models.event.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
     @Query("SELECT t FROM Ticket t WHERE t.customer.customerNumber = :customerNumber")
     List<Ticket> findAllTicketsByCustomerNumber(String customerNumber);

     @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId")
     List<Ticket> findAllByEventId(Long eventId);

     @Query("SELECT t FROM Ticket t WHERE t.customer.user.id = :userId")
     List<Ticket> findAllByUserId(Long userId);

     @Query("SELECT t FROM Ticket t WHERE t.customer.user.id = :userId")
     Page<Ticket> findAllByUserId(Long userId, Pageable pageable);

     @Query("SELECT t FROM Ticket t WHERE t.customer.user.id = :userId AND t.event.id = :eventId")
     List<Ticket> findAllByUserIdAndEventId(Long userId, Long eventId);
}
