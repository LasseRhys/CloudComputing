package API.EventTom;

import API.EventTom.models.event.Event;
import API.EventTom.models.event.Ticket;
import API.EventTom.services.tickets.ThresholdPriceStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThresholdPriceStrategyPhase1Test {

    private final ThresholdPriceStrategy strategy = new ThresholdPriceStrategy();

    @Test
    void shouldIncreasePriceByTwentyPercentWhenThresholdIsReached() {
        Event event = new Event();
        event.setThresholdValue(2);

        List<Ticket> soldTickets = new ArrayList<>();
        soldTickets.add(new Ticket());
        soldTickets.add(new Ticket());
        event.setTickets(soldTickets);

        BigDecimal calculated = strategy.calculatePrice(event, BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(120.0), calculated);
    }
}
