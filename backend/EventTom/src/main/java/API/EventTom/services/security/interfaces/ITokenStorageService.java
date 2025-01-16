package API.EventTom.services.security.interfaces;

import API.EventTom.models.user.RefreshToken;

import java.util.Optional;

public interface ITokenStorageService {
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyAndRotateToken(String token);
    void deleteToken(String token);
    Optional<RefreshToken> findToken(String token);
    void cleanupExpiredTokens();
}
