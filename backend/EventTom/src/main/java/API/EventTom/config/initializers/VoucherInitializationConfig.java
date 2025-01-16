package API.EventTom.config.initializers;

import API.EventTom.dto.request.BulkVoucherGenerationRequestDTO;
import API.EventTom.dto.request.VoucherGenerationRequestDTO;
import API.EventTom.models.event.Voucher;
import API.EventTom.models.event.VoucherType;
import API.EventTom.services.vouchers.interfaces.IVoucherGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class VoucherInitializationConfig {

    private final IVoucherGenerationService voucherGenerationService;

    @Bean
    @Order(3)
    public CommandLineRunner initializeVouchers() {
        return args -> {
            createPromotionalVouchers();
            createLoyaltyVouchers();
            createSpecialEventVouchers();
            createBulkDiscountVouchers();
        };
    }

    private void createPromotionalVouchers() {
        try {
            BulkVoucherGenerationRequestDTO requestDTO = new BulkVoucherGenerationRequestDTO();
            requestDTO.setAmount(new BigDecimal("20.00"));
            requestDTO.setValidityDays(30);
            requestDTO.setType(VoucherType.PROMOTIONAL);
            requestDTO.setCount(5);

            List<Voucher> promoVouchers = voucherGenerationService.generateBulkVouchers(requestDTO);
            System.out.println("Created " + promoVouchers.size() + " promotional vouchers");
        } catch (Exception e) {
            System.err.println("Failed to create promotional vouchers: " + e.getMessage());
        }
    }

    private void createLoyaltyVouchers() {
        try {
            BulkVoucherGenerationRequestDTO requestDTO = new BulkVoucherGenerationRequestDTO();
            requestDTO.setAmount(new BigDecimal("50.00"));
            requestDTO.setValidityDays(60);
            requestDTO.setType(VoucherType.LOYALTY);
            requestDTO.setCount(3);

            List<Voucher> loyaltyVouchers = voucherGenerationService.generateBulkVouchers(requestDTO);
            System.out.println("Created " + loyaltyVouchers.size() + " loyalty vouchers");
        } catch (Exception e) {
            System.err.println("Failed to create loyalty vouchers: " + e.getMessage());
        }
    }

    private void createSpecialEventVouchers() {
        try {
            BulkVoucherGenerationRequestDTO requestDTO = new BulkVoucherGenerationRequestDTO();
            requestDTO.setAmount(new BigDecimal("15.00"));
            requestDTO.setValidityDays(14);
            requestDTO.setType(VoucherType.PROMOTIONAL);
            requestDTO.setCount(10);

            List<Voucher> specialEventVouchers = voucherGenerationService.generateBulkVouchers(requestDTO);
            System.out.println("Created " + specialEventVouchers.size() + " special event vouchers");
        } catch (Exception e) {
            System.err.println("Failed to create special event vouchers: " + e.getMessage());
        }
    }

    private void createBulkDiscountVouchers() {
        try {
            BulkVoucherGenerationRequestDTO smallBulkDTO = new BulkVoucherGenerationRequestDTO();
            smallBulkDTO.setAmount(new BigDecimal("25.00"));
            smallBulkDTO.setValidityDays(45);
            smallBulkDTO.setType(VoucherType.PROMOTIONAL);
            smallBulkDTO.setCount(3);
            List<Voucher> smallBulkVouchers = voucherGenerationService.generateBulkVouchers(smallBulkDTO);

            BulkVoucherGenerationRequestDTO mediumBulkDTO = new BulkVoucherGenerationRequestDTO();
            mediumBulkDTO.setAmount(new BigDecimal("75.00"));
            mediumBulkDTO.setValidityDays(45);
            mediumBulkDTO.setType(VoucherType.PROMOTIONAL);
            mediumBulkDTO.setCount(2);
            List<Voucher> mediumBulkVouchers = voucherGenerationService.generateBulkVouchers(mediumBulkDTO);

            BulkVoucherGenerationRequestDTO largeBulkDTO = new BulkVoucherGenerationRequestDTO();
            largeBulkDTO.setAmount(new BigDecimal("100.00"));
            largeBulkDTO.setValidityDays(45);
            largeBulkDTO.setType(VoucherType.PROMOTIONAL);
            largeBulkDTO.setCount(1);
            List<Voucher> largeBulkVouchers = voucherGenerationService.generateBulkVouchers(largeBulkDTO);

            System.out.println("Created bulk discount vouchers of different tiers");
        } catch (Exception e) {
            System.err.println("Failed to create bulk discount vouchers: " + e.getMessage());
        }
    }
}