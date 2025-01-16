package API.EventTom.dto.response;

import java.util.Set;

public record LoginResponseDTO(
        Long id,
        String email,
        Set<String> roles
) {}