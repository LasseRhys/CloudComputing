package API.EventTom.services.vouchers;

import API.EventTom.models.user.Customer;
import API.EventTom.models.event.Voucher;
import API.EventTom.repositories.CustomerRepository;
import API.EventTom.repositories.VoucherRepository;
import API.EventTom.services.vouchers.interfaces.IVoucherClaimService;
import API.EventTom.services.vouchers.interfaces.IVoucherValidationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
class VoucherClaimServiceImpl implements IVoucherClaimService {
    private final VoucherRepository voucherRepository;
    private final CustomerRepository customerRepository;
    private final IVoucherValidationService validationService;

    @Override
    @Transactional
    public Voucher claimVoucher(String code, Long customerId) {
        Voucher voucher = validationService.validateVoucherExists(code);
        validationService.validateVoucherNotClaimed(voucher);
        validationService.validateVoucherNotExpired(voucher);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        voucher.setCustomer(customer);
        return voucherRepository.save(voucher);
    }

    @Override
    public boolean isVoucherClaimable(String code) {
        return voucherRepository.findByCode(code)
                .map(voucher -> !voucher.isUsed()
                        && voucher.getCustomer() == null
                        && voucher.getExpirationDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}
