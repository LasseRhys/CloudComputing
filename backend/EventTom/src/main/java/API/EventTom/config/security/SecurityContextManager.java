package API.EventTom.config.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextManager {
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }
}