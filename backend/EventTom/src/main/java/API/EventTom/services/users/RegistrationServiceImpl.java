package API.EventTom.services.users;

import API.EventTom.dto.request.EmployeeRegisterRequestDTO;
import API.EventTom.dto.request.CustomerRegisterRequestDTO;
import API.EventTom.dto.response.RegisterResponseDTO;
import API.EventTom.exceptions.userExceptions.EmailAlreadyExistsException;
import API.EventTom.models.user.Customer;
import API.EventTom.models.user.Employee;
import API.EventTom.models.user.User;
import API.EventTom.models.user.UserType;
import API.EventTom.repositories.CustomerRepository;
import API.EventTom.repositories.EmployeeRepository;
import API.EventTom.repositories.UserRepository;
import API.EventTom.services.users.interfaces.IRegistrationService;
import API.EventTom.services.users.interfaces.IRoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements IRegistrationService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserNumberGenerator userNumberGenerator;
    private final IRoleManagementService roleManagementService;

    @Override
    @Transactional
    public RegisterResponseDTO registerCustomer(CustomerRegisterRequestDTO request) {
        validateEmailUniqueness(request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email " + request.email() + " is already in use");
        }

        User user = createBaseUser(request.email(), request.password(), request.firstName(), request.lastName(), UserType.CUSTOMER);
        user.setRoles(roleManagementService.getDefaultRoles());

        Customer customer = new Customer();
        customer.setUser(user);

        customer.setCustomerNumber(userNumberGenerator.generateCustomerNumber());

        user = userRepository.save(user);
        customer = customerRepository.save(customer);

        return createRegisterResponse(user);
    }

    @Override
    @Transactional
    public RegisterResponseDTO registerEmployee(EmployeeRegisterRequestDTO request) {
        validateEmailUniqueness(request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email " + request.email() + " is already in use");
        }

        User user = createBaseUser(request.email(), request.password(), request.firstName(), request.lastName(), UserType.EMPLOYEE);
        user.setRoles(roleManagementService.getRolesByNames(request.roles()));

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setEmployeeNumber(userNumberGenerator.generateEmployeeNumber());

        user = userRepository.save(user);
        employee = employeeRepository.save(employee);

        return createRegisterResponse(user);
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("User already exists with email: " + email);
        }
    }

    private User createBaseUser(String email, String password, String firstname, String lastname, UserType userType) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setUserType(userType);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        return user;
    }

    private RegisterResponseDTO createRegisterResponse(User user) {
        return new RegisterResponseDTO(
                user.getId(),
                user.getEmail(),
                "User registered successfully"
        );
    }
}