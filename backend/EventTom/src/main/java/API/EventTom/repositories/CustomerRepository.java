package API.EventTom.repositories;


import API.EventTom.dto.response.NextEventDTO;
import API.EventTom.models.event.Voucher;
import API.EventTom.models.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
     Optional<Customer> findCustomerByCustomerNumber(String customerNumber);
     Optional<Customer> findByUserId(Long userId);

     @Query("SELECT new API.EventTom.dto.response.NextEventDTO(e.title, e.dateOfEvent, e.location) " +
             "FROM Customer c " +
             "JOIN c.tickets t " +
             "JOIN t.event e " +
             "WHERE c.user.id = :userId " +
             "AND e.dateOfEvent > CURRENT_TIMESTAMP " +
             "ORDER BY e.dateOfEvent ASC " +
             "LIMIT 1")
     Optional<NextEventDTO> findNextEventByUserId(@Param("userId") Long userId);

     @Query("SELECT COUNT(v) FROM Customer c " +
             "JOIN c.vouchers v " +
             "WHERE c.user.id = :userId " +
             "AND v.used = false " +
             "GROUP BY c.id")
     Long countAllVoucherByUserIdAndUnused(@Param("userId") Long userId);

     @Query("SELECT SUM(v.amount) FROM Customer c " +
             "JOIN c.vouchers v " +
             "WHERE c.user.id = :userId " +
             "AND v.used = false " +
             "GROUP BY c.id")
     BigDecimal sumAllVoucherValuesByUserIdAndUnused(@Param("userId") Long userId);;

     @Query("SELECT COUNT(DISTINCT t.event) FROM Customer c JOIN c.tickets t WHERE c.user.id = :userId")
     Long countAllEventsByUserId(@Param("userId") Long userId);
}
