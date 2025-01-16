package API.EventTom.repositories;

import API.EventTom.models.user.Employee;
import API.EventTom.models.event.Event;
import API.EventTom.models.user.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findEmployeeByEmployeeNumber(String number);
    @Query("SELECT DISTINCT e FROM Employee e " +
            "JOIN e.user u " +
            "JOIN u.roles r " +
            "WHERE r.name = API.EventTom.models.user.Roles.EVENT_MANAGER")
    List<Employee> findAllEventManagers();
    @Query("SELECT e FROM Employee e  WHERE :event MEMBER OF e.managedEvents")
    List<Employee> findAllByEvent(@Param("event") Event event);

}
