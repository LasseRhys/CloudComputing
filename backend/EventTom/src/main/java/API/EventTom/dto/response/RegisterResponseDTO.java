package API.EventTom.dto.response;


public record RegisterResponseDTO(
        Long id,
        String email,
        String message
) {}