package API.EventTom;

import API.EventTom.models.event.Event;
import API.EventTom.models.user.Employee;
import API.EventTom.models.user.User;
import API.EventTom.models.user.UserType;
import API.EventTom.repositories.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class ApplicationContextPhase2IntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistAndLoadEventViaRepository() {
        User creatorUser = new User();
        creatorUser.setEmail("creator@test.local");
        creatorUser.setPassword("secret");
        creatorUser.setFirstName("Test");
        creatorUser.setLastName("Creator");
        creatorUser.setUserType(UserType.EMPLOYEE);

        Employee creator = new Employee();
        creator.setUser(creatorUser);
        creator.setEmployeeNumber("EMP-1001");

        entityManager.persist(creatorUser);
        entityManager.persist(creator);

        Event event = new Event();
        event.setTitle("Integration Test Event");
        event.setDateOfEvent(LocalDateTime.now().plusDays(7));
        event.setLocation("Hamburg");
        event.setMaxTotalTickets(200);
        event.setThresholdValue(50);
        event.setBasePrice(BigDecimal.valueOf(49.99));
        event.setCreator(creator);

        Event saved = eventRepository.save(event);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreator());
        assertEquals("EMP-1001", saved.getCreator().getEmployeeNumber());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals("Integration Test Event", eventRepository.findAll().get(0).getTitle());
    }
}
