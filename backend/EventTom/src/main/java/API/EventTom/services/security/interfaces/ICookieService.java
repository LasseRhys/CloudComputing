package API.EventTom.services.security.interfaces;

import org.springframework.http.ResponseCookie;

public interface ICookieService {
    ResponseCookie createAuthCookie(String token);
    ResponseCookie createRefreshCookie(String token);
    ResponseCookie clearAuthCookie();
    ResponseCookie clearRefreshCookie();
}
