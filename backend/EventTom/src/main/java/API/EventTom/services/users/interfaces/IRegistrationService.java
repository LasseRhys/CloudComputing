package API.EventTom.services.users.interfaces;

import API.EventTom.dto.request.CustomerRegisterRequestDTO;
import API.EventTom.dto.request.EmployeeRegisterRequestDTO;
import API.EventTom.dto.response.RegisterResponseDTO;

public interface IRegistrationService {
    RegisterResponseDTO registerCustomer(CustomerRegisterRequestDTO request);
    RegisterResponseDTO registerEmployee(EmployeeRegisterRequestDTO request);
}