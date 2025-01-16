package API.EventTom.services.tickets;

import API.EventTom.models.event.Event;
import API.EventTom.repositories.EventRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ThresholdPriceStrategy implements IPriceStrategy {
    private static final BigDecimal THRESHOLD_MULTIPLIER = BigDecimal.valueOf(1.2);


    @Override
    public BigDecimal calculatePrice(Event event, BigDecimal basePrice) {
        if (event.isThresholdReached()) {
            return basePrice.multiply(THRESHOLD_MULTIPLIER);
        }
        return basePrice;
    }
}