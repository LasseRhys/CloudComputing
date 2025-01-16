package API.EventTom.repositories;
import API.EventTom.models.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import API.EventTom.models.user.User;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);


    @Modifying
    @Query("DELETE FROM refreshtoken t WHERE t.expiryDate <= :currentInstant")
    void deleteAllExpiredTokens(Instant currentInstant);

}