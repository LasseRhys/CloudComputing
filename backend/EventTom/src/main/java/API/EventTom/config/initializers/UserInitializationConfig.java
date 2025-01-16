package API.EventTom.config.initializers;

import API.EventTom.dto.request.CustomerRegisterRequestDTO;
import API.EventTom.dto.request.EmployeeRegisterRequestDTO;
import API.EventTom.models.user.Roles;
import API.EventTom.services.users.RegistrationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.EnumSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class UserInitializationConfig {

    private final RegistrationServiceImpl registrationService;

    @Bean
    @Order(1)
    public CommandLineRunner initializeUsers() {
        return args -> {
            createAdminEmployee();
            createEventManagerEmployee();
            createSalesRepEmployee();

            createSampleCustomers();
        };
    }

    private void createAdminEmployee() {
        Set<Roles> adminRoles = EnumSet.of(
                Roles.ADMINISTRATOR,
                Roles.EVENT_CREATOR,
                Roles.EVENT_MANAGER
        );

        EmployeeRegisterRequestDTO adminRequest = new EmployeeRegisterRequestDTO(
                "admin@eventtom.com",
                "Admin123!",
                "Admin",
                "User",
                adminRoles
        );

        try {
            registrationService.registerEmployee(adminRequest);
        } catch (Exception e) {
            System.err.println("Failed to create admin employee: " + e.getMessage());
        }
    }

    private void createEventManagerEmployee() {
        Set<Roles> managerRoles = EnumSet.of(Roles.EVENT_MANAGER);
        EmployeeRegisterRequestDTO managerRequest = new EmployeeRegisterRequestDTO(
                "manager@eventtom.com",
                "password123",
                "Event",
                "Manager",
                managerRoles
        );

        Set<Roles> creatorRoles = EnumSet.of(Roles.EVENT_CREATOR);
        EmployeeRegisterRequestDTO creatorRequest = new EmployeeRegisterRequestDTO(
                "creator@eventtom.com",
                "password123",
                "Event",
                "Creator",
                creatorRoles
        );

        try {
            registrationService.registerEmployee(managerRequest);
            registrationService.registerEmployee(creatorRequest);
        } catch (Exception e) {
            System.err.println("Failed to create employees: " + e.getMessage());
        }
    }
    private void createSalesRepEmployee() {
        Set<Roles> salesRoles = EnumSet.of(
                Roles.SALES_REP
        );

        EmployeeRegisterRequestDTO salesRequest = new EmployeeRegisterRequestDTO(
                "sales@eventtom.com",
                "Sales123!",
                "Sales",
                "Representative",
                salesRoles
        );

        try {
            registrationService.registerEmployee(salesRequest);
        } catch (Exception e) {
            System.err.println("Failed to create sales employee: " + e.getMessage());
        }
    }

    private void createSampleCustomers() {
        CustomerRegisterRequestDTO[] customerRequests = {
                createCustomerRequest(
                        "john.doe@example.com",
                        "Customer123!",
                        "John",
                        "Doe"
                ),
                createCustomerRequest(
                        "jane.smith@example.com",
                        "Customer123!",
                        "Jane",
                        "Smith"
                ),
                createCustomerRequest(
                        "bob.wilson@example.com",
                        "Customer123!",
                        "Bob",
                        "Wilson"
                )
        };

        for (CustomerRegisterRequestDTO request : customerRequests) {
            try {
                registrationService.registerCustomer(request);
            } catch (Exception e) {
                System.err.println("Failed to create customer: " + request.email());
            }
        }
    }

    private CustomerRegisterRequestDTO createCustomerRequest(
            String email,
            String password,
            String firstName,
            String lastName
    ) {
        return new CustomerRegisterRequestDTO(email, password, firstName, lastName);
    }
}