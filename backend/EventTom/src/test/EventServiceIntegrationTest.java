package com.eventtom.backend.service;

import com.eventtom.backend.model.Event;
import com.eventtom.backend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EventServiceIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        testEvent = new Event();
        testEvent.setName("Test Konzert");
        testEvent.setDescription("Ein Test-Event für Integration Tests");
        testEvent.setLocation("Berlin");
        testEvent.setDate(LocalDateTime.now().plusDays(30));
        testEvent.setTicketPrice(49.99);
        testEvent.setAvailableTickets(100);
    }

    @Test
    void testCreateAndRetrieveEvent() {
        Event created = eventService.createEvent(testEvent);

        assertNotNull(created.getId());
        assertEquals("Test Konzert", created.getName());

        Optional<Event> retrieved = eventService.findById(created.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(created.getId(), retrieved.get().getId());
    }

    @Test
    void testUpdateEventTicketCount() {
        Event created = eventService.createEvent(testEvent);
        Long eventId = created.getId();

        int ticketsToPurchase = 5;
        eventService.purchaseTickets(eventId, ticketsToPurchase);

        Event updated = eventService.findById(eventId).orElseThrow();
        assertEquals(95, updated.getAvailableTickets());
    }

    @Test
    void testFindAllEvents() {
        eventService.createEvent(testEvent);

        Event anotherEvent = new Event();
        anotherEvent.setName("Zweites Event");
        anotherEvent.setDescription("Noch ein Event");
        anotherEvent.setLocation("München");
        anotherEvent.setDate(LocalDateTime.now().plusDays(60));
        anotherEvent.setTicketPrice(29.99);
        anotherEvent.setAvailableTickets(50);
        eventService.createEvent(anotherEvent);

        List<Event> events = eventService.findAllEvents();
        assertEquals(2, events.size());
    }

    @Test
    void testDeleteEvent() {
        Event created = eventService.createEvent(testEvent);
        Long eventId = created.getId();

        eventService.deleteEvent(eventId);

        Optional<Event> deleted = eventService.findById(eventId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testInsufficientTickets() {
        testEvent.setAvailableTickets(3);
        Event created = eventService.createEvent(testEvent);

        assertThrows(IllegalStateException.class, () -> {
            eventService.purchaseTickets(created.getId(), 5);
        });
    }
}