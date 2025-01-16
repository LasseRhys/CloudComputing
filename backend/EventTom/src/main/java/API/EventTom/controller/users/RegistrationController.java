package API.EventTom.controller.users;

import API.EventTom.dto.request.CustomerRegisterRequestDTO;
import API.EventTom.dto.request.EmployeeRegisterRequestDTO;
import API.EventTom.dto.response.RegisterResponseDTO;
import API.EventTom.services.users.interfaces.IRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final IRegistrationService registrationService;

    @PostMapping("/customer")
    public ResponseEntity<RegisterResponseDTO> registerCustomer(@Valid @RequestBody CustomerRegisterRequestDTO request) {
        return ResponseEntity.ok(registrationService.registerCustomer(request));
    }

    @PostMapping("/employee")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<RegisterResponseDTO> registerEmployee(@Valid @RequestBody EmployeeRegisterRequestDTO request) {
        return ResponseEntity.ok(registrationService.registerEmployee(request));
    }
}