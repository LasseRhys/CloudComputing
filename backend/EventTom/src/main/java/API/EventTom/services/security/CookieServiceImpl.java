package API.EventTom.services.security;

import API.EventTom.services.security.interfaces.ICookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements ICookieService {
    @Value("${eventtom.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${eventtom.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Override
    public ResponseCookie createAuthCookie(String token) {
        return createCookie("JWT_TOKEN", token, jwtExpirationMs);
    }

    @Override
    public ResponseCookie createRefreshCookie(String token) {
        return createCookie("REFRESH_TOKEN", token, jwtRefreshExpirationMs);
    }

    @Override
    public ResponseCookie clearAuthCookie() {
        return createCookie("JWT_TOKEN", "", 0);
    }

    @Override
    public ResponseCookie clearRefreshCookie() {
        return createCookie("REFRESH_TOKEN", "", 0);
    }

    private ResponseCookie createCookie(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
