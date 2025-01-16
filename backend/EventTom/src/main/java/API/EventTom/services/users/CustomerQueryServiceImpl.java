package API.EventTom.services.users;

import API.EventTom.dto.CustomerDTO;
import API.EventTom.dto.response.DashboardInformation;
import API.EventTom.dto.response.EventDashboardDTO;
import API.EventTom.dto.response.NextEventDTO;
import API.EventTom.dto.response.VoucherDashboardDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.user.Customer;
import API.EventTom.repositories.CustomerRepository;
import API.EventTom.services.common.BaseQueryService;
import API.EventTom.services.users.interfaces.ICustomerQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerQueryServiceImpl extends BaseQueryService<Customer, CustomerDTO, Long>
        implements ICustomerQueryService {


    private final CustomerRepository customerRepository;

    public CustomerQueryServiceImpl(
            CustomerRepository customerRepository,
            StandardDTOMapper standardDTOMapper) {
        super(customerRepository,
                standardDTOMapper,
                standardDTOMapper::mapCustomerToCustomerDTO,
                "Customer");
        this.customerRepository = customerRepository;
    }

    public CustomerDTO getCustomerByCustomerNumber(String customerNumber) {
        return ((CustomerRepository) repository).findCustomerByCustomerNumber(customerNumber)
                .map(mapperFunction)
                .orElseThrow(() -> new RuntimeException("Customer not found with Customer Number: " + customerNumber));
    }

    public DashboardInformation getCustomerDashboardInformationByUserId(Long userId) {
        Long voucherCount = customerRepository.countAllVoucherByUserIdAndUnused(userId);
        BigDecimal voucherValue = customerRepository.sumAllVoucherValuesByUserIdAndUnused(userId);
        if (voucherValue == null) {
            voucherValue = BigDecimal.ZERO;
        }

        Long eventCount = customerRepository.countAllEventsByUserId(userId);
        NextEventDTO nextEvent = customerRepository.findNextEventByUserId(userId).orElse(null);

        return new DashboardInformation(
                new VoucherDashboardDTO(voucherCount, voucherValue),
                new EventDashboardDTO(eventCount),
                nextEvent
        );
    }

    @Override
    public List<CustomerDTO> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Page<CustomerDTO> findAllByUserId(Long userId, Pageable pageable) {
        return Page.empty(pageable);
    }
}