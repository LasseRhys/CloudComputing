package API.EventTom.dto;

import API.EventTom.dto.response.UserDTO;
import API.EventTom.models.user.Roles;

import java.util.Set;

public record EmployeeDTO(
        Set<Roles> roles,
        String employeeNumber,
        UserDTO user
) {}
