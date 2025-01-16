package API.EventTom.services.security;
import API.EventTom.dto.request.LoginRequestDTO;
import API.EventTom.dto.response.LoginResponseDTO;
import API.EventTom.dto.response.TokenRefreshResponseDTO;
import API.EventTom.config.security.SecurityContextManager;
import API.EventTom.config.security.jwt.JwtUtils;
import API.EventTom.exceptions.tokenException.TokenProcessingException;
import API.EventTom.exceptions.tokenException.TokenRefreshException;
import API.EventTom.models.user.RefreshToken;
import API.EventTom.models.user.User;
import API.EventTom.models.user.UserDetailsImpl;
import API.EventTom.services.security.interfaces.IAuthenticationService;
import API.EventTom.services.security.interfaces.ICookieService;
import API.EventTom.services.security.interfaces.ITokenStorageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ITokenStorageService tokenStorageService;
    private final ICookieService cookieService;
    private final SecurityContextManager securityContextManager;

    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);
        RefreshToken refreshToken = tokenStorageService.createRefreshToken(userDetails.getId());

        response.addHeader("Set-Cookie", cookieService.createAuthCookie(jwt).toString());
        response.addHeader("Set-Cookie", cookieService.createRefreshCookie(refreshToken.getToken()).toString());

        return new LoginResponseDTO(userDetails.getId(), userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
    }

    @Override
    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        securityContextManager.clearContext();

        if (refreshToken != null && !refreshToken.isEmpty()) {
            tokenStorageService.deleteToken(refreshToken);
        }

        response.addHeader("Set-Cookie", cookieService.clearAuthCookie().toString());
        response.addHeader("Set-Cookie", cookieService.clearRefreshCookie().toString());
    }



    @Override
    @Transactional
    public TokenRefreshResponseDTO refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenProcessingException("Refresh token is missing");
        }

        try {
            RefreshToken newRefreshToken = tokenStorageService.verifyAndRotateToken(refreshToken);
            User user = newRefreshToken.getUser();

            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList());

            String newAccessToken = jwtUtils.generateTokenFromEmailAndRoles(user.getEmail(), roles);

            response.addHeader("Set-Cookie", cookieService.createAuthCookie(newAccessToken).toString());
            response.addHeader("Set-Cookie", cookieService.createRefreshCookie(newRefreshToken.getToken()).toString());

            return new TokenRefreshResponseDTO(newAccessToken, newRefreshToken.getToken());

        } catch (TokenRefreshException e) {
            response.addHeader("Set-Cookie", cookieService.clearAuthCookie().toString());
            response.addHeader("Set-Cookie", cookieService.clearRefreshCookie().toString());
            throw new TokenProcessingException("Failed to refresh token: " + e.getMessage());
        } catch (Exception e) {
            throw new TokenProcessingException("Unexpected error during token refresh: " + e.getMessage());
        }
    }

}