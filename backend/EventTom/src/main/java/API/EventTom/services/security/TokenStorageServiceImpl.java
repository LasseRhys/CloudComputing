package API.EventTom.services.security;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


import API.EventTom.exceptions.notFoundExceptions.UserNotFoundException;
import API.EventTom.exceptions.tokenException.TokenRefreshException;
import API.EventTom.models.user.RefreshToken;
import API.EventTom.models.user.User;
import API.EventTom.repositories.RefreshTokenRepository;
import API.EventTom.repositories.UserRepository;
import API.EventTom.services.security.interfaces.ITokenStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TokenStorageServiceImpl implements ITokenStorageService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${eventtom.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        RefreshToken token = buildRefreshToken(user);
        return refreshTokenRepository.save(token);
    }

    private RefreshToken buildRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setExpiryDate(calculateExpiryDate());
        token.setToken(generateTokenString());
        return token;
    }

    @Override
    @Transactional
    public RefreshToken verifyAndRotateToken(String token) {
        RefreshToken refreshToken = findToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Token not found"));

        verifyTokenExpiration(refreshToken);
        return rotateToken(refreshToken);
    }

    @Override
    @Transactional
    public void deleteToken(String token) {
        findToken(token).ifPresent(refreshToken -> {
            try {
                refreshTokenRepository.delete(refreshToken);
            } catch (Exception e) {
                throw new TokenRefreshException(token, "Error deleting token: " + e.getMessage());
            }
        });
    }

    @Override
    public Optional<RefreshToken> findToken(String token) {
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Scheduled(cron = "0 0 18 * * ?", zone = "Europe/Berlin")
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
        } catch (Exception e) {
            System.err.println("Error during token cleanup: " + e.getMessage());
        }
    }

    private void verifyTokenExpiration(RefreshToken token) {
        if (isTokenExpired(token)) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Token expired");
        }
    }

    private boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().compareTo(Instant.now()) < 0;
    }

    private RefreshToken rotateToken(RefreshToken token) {
        token.setToken(generateTokenString());
        token.setExpiryDate(calculateExpiryDate());
        return refreshTokenRepository.save(token);
    }

    private String generateTokenString() {
        return UUID.randomUUID().toString();
    }

    private Instant calculateExpiryDate() {
        return Instant.now().plusMillis(refreshTokenDurationMs);
    }
}