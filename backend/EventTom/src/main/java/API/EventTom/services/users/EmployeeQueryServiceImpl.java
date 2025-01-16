package API.EventTom.services.users;

import API.EventTom.dto.CustomerDTO;
import API.EventTom.dto.EmployeeDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.user.Customer;
import API.EventTom.models.user.Employee;
import API.EventTom.repositories.EmployeeRepository;
import API.EventTom.services.common.BaseQueryService;
import API.EventTom.services.users.interfaces.IEmployeeQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeQueryServiceImpl extends BaseQueryService<Employee, EmployeeDTO, Long> implements IEmployeeQueryService {

    public EmployeeQueryServiceImpl(
            EmployeeRepository employeeRepository,
            StandardDTOMapper standardDTOMapper) {
        super(employeeRepository,
                standardDTOMapper,
                standardDTOMapper::mapEmployeeToEmployeeDTO,
                "Employee");
    }

    public EmployeeDTO getEmployeeByEmployeeNumber(String employeeNumber) {
        return ((EmployeeRepository) repository).findEmployeeByEmployeeNumber(employeeNumber)
                .map(mapperFunction)
                .orElseThrow(() -> new RuntimeException("Employee not found with Employee Number: " + employeeNumber));
    }

    @Override
    public List<EmployeeDTO> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<EmployeeDTO> getAllEventManagers() {
        return ((EmployeeRepository) repository).findAllEventManagers()
                .stream()
                .map(mapperFunction)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDTO> findAllByUserId(Long userId, Pageable pageable) {
        return Page.empty(pageable);
    }
}


