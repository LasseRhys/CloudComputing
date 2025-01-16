package API.EventTom.services.tickets;

import API.EventTom.models.event.Event;
import API.EventTom.models.event.Voucher;
import API.EventTom.services.tickets.interfaces.ITicketPriceCalculator;
import API.EventTom.services.vouchers.interfaces.IVoucherUsageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketPriceCalculatorImpl implements ITicketPriceCalculator {
    private final List<IPriceStrategy> priceStrategies;
    private final IVoucherUsageService voucherUsageService;

    @Override
    public BigDecimal calculateBasePrice(Event event) {
        BigDecimal basePrice = event.getBasePrice();
        for (IPriceStrategy strategy : priceStrategies) {
            basePrice = strategy.calculatePrice(event, basePrice);
        }
        return basePrice;
    }

    @Override
    public BigDecimal calculateTotalPrice(Event event, int amount, List<String> voucherCodes) {
        BigDecimal baseTicketPrice = calculateBasePrice(event);
        BigDecimal totalPrice = baseTicketPrice.multiply(BigDecimal.valueOf(amount));

        if (voucherCodes != null && !voucherCodes.isEmpty()) {
            List<Voucher> vouchers = voucherUsageService.validateVouchers(voucherCodes);
            BigDecimal totalDiscount = voucherUsageService.calculateTotalDiscount(vouchers);
            return totalPrice.subtract(totalDiscount).max(BigDecimal.ZERO);
        }

        return totalPrice;
    }
}