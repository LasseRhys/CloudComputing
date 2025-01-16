package API.EventTom.services.security.interfaces;

import jakarta.servlet.http.HttpServletRequest;

public interface ICookieExtractorService {
    String extractRefreshToken(HttpServletRequest request);
}