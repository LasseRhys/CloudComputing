package API.EventTom.config.initializers;

import API.EventTom.models.user.Role;
import API.EventTom.models.user.Roles;
import API.EventTom.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;

@Configuration
@AllArgsConstructor
public class RoleConfig {

    private final RoleRepository roleRepository;
    @Bean
    @Order(0)
    CommandLineRunner initRoles() {
        return args -> {
            if (roleRepository.count() == 0) {
                Arrays.stream(Roles.values())
                        .forEach(role -> roleRepository.save(Role.from(role)));
            }
        };
    }
}