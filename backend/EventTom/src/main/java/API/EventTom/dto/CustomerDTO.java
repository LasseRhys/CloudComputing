package API.EventTom.dto;

import API.EventTom.dto.response.UserDTO;

import java.util.List;
public record CustomerDTO(
        UserDTO user,
        String customerNumber
) {}