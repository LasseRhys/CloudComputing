package API.EventTom.dto.response;

public record TokenRefreshResponseDTO(
        String accessToken,
        String refreshToken
) {}
