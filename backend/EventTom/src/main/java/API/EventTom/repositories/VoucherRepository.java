package API.EventTom.repositories;

import API.EventTom.models.event.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT v FROM Voucher v WHERE v.code = :code")
    Optional<Voucher> findByCode(@Param("code") String code);

    @Query("SELECT v FROM Voucher v WHERE v.customer.user.id = :userId")
    List<Voucher> findByUserId(@Param("userId") Long userId);

    @Query("SELECT v FROM Voucher v " +
            "JOIN v.customer c " +
            "WHERE c.user.id = :userId")
    Page<Voucher> findByUserId(@Param("userId") Long userId, Pageable pageable);

    boolean existsByCode(String code);


}
