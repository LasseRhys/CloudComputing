package API.EventTom.controller.users;

import API.EventTom.dto.request.LoginRequestDTO;
import API.EventTom.dto.response.LoginResponseDTO;
import API.EventTom.dto.response.MessageResponseDTO;
import API.EventTom.dto.response.TokenRefreshResponseDTO;
import API.EventTom.services.security.interfaces.IAuthenticationService;
import API.EventTom.services.security.interfaces.ICookieExtractorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final IAuthenticationService authenticationService;
    private final ICookieExtractorService cookieExtractorService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO loginResponseDTO = authenticationService.authenticate(request, response);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponseDTO> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(cookieExtractorService.extractRefreshToken(request), response);
        return ResponseEntity.ok(new MessageResponseDTO("You've been signed out!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponseDTO> refreshJwtToken(HttpServletRequest request, HttpServletResponse response) {
        TokenRefreshResponseDTO tokenRefresh = authenticationService.refreshToken(cookieExtractorService.extractRefreshToken(request), response);
        return ResponseEntity.ok(tokenRefresh);
    }

    @GetMapping("authenticated")
    public ResponseEntity<Boolean> isAuthenticated() {
        return ResponseEntity.ok(true);
    }

}