package API.EventTom.repositories;

import API.EventTom.models.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByManagers_Id(Long managerId);
    Page<Event> findByManagers_Id(Long managerId, Pageable pageable);

    default Specification<Event> hasManagerId(Long managerId) {
        return (root, query, cb) -> {
            return cb.equal(root.join("managers").get("id"), managerId);
        };
    }
}