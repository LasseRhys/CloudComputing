package API.EventTom.repositories;

import API.EventTom.models.Notification;
import API.EventTom.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndReadOrderByCreatedAtDesc(User recipient, boolean isRead);

    List<Notification> findAllByUserId(long userId);
    Page<Notification> findAllByUserId(long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user")
    void markAllAsRead(@Param("user") User user);
}