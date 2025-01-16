package API.EventTom.controller.users;

import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.dto.CustomerDTO;
import API.EventTom.dto.response.DashboardInformation;
import API.EventTom.services.users.interfaces.ICustomerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final ICustomerQueryService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customerDTOList = customerService.getAll();
        return ResponseEntity.ok(customerDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO customerDTO = customerService.getById(id);
        return ResponseEntity.ok(customerDTO);
    }

    @GetMapping("/user")
    public ResponseEntity<CustomerDTO> getLoggedInCustomerById(@AuthenticatedUserId Long userId) {
        CustomerDTO customerDTO = customerService.getById(userId);
        return ResponseEntity.ok(customerDTO);
    }

    @GetMapping("/dashboard-information")
    public ResponseEntity<DashboardInformation> getDashboardInformation(@AuthenticatedUserId Long userId) {
        return ResponseEntity.ok(customerService.getCustomerDashboardInformationByUserId(userId));
    }
}