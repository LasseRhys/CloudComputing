package API.EventTom.services.users;


import org.springframework.stereotype.Component;

@Component
public class UserNumberGenerator {
    public String generateCustomerNumber() {
        return "C" + System.currentTimeMillis();
    }

    public String generateEmployeeNumber() {
        return "E" + System.currentTimeMillis();
    }
}
