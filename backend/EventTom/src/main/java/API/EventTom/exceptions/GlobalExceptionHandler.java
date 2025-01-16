package API.EventTom.exceptions;

import API.EventTom.dto.response.ErrorMessageDTO;

import API.EventTom.exceptions.notFoundExceptions.ResourceNotFoundException;
import API.EventTom.exceptions.tokenException.TokenRefreshException;
import API.EventTom.exceptions.tokenException.UserNotAuthenticatedException;
import API.EventTom.exceptions.userExceptions.EmailAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.util.stream.Collectors;

import static API.EventTom.exceptions.ExceptionUtils.buildResponseEntity;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDTO> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return buildResponseEntity(errorMessage, HttpStatus.BAD_REQUEST, e);
    }
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorMessageDTO> handleTokenRefreshException(TokenRefreshException e) {
        return buildResponseEntity("Invalid refresh token", HttpStatus.UNAUTHORIZED, e);
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return buildResponseEntity("Page does not Exist", HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessageDTO> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return buildResponseEntity("Error handling Data", HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return buildResponseEntity("Error handling Data", HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageDTO> handleBadCredentialsException(BadCredentialsException e) {
        return buildResponseEntity("Invalid username or password", HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessageDTO> handleAuthenticationException(AuthenticationException e) {
        return buildResponseEntity("Authentication failed", HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAccessDeniedException(AccessDeniedException e) {
        return buildResponseEntity("Access denied", HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserNotAuthenticatedException(UserNotAuthenticatedException e) {
        return buildResponseEntity("User not authenticated", HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodNotAllowedException(Exception e) {
        return buildResponseEntity("Method not allowed", HttpStatus.METHOD_NOT_ALLOWED, e);

    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorMessageDTO> handleJwtException(JwtException e) {
        return buildResponseEntity("Invalid token", HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleResourceNotFoundException(ResourceNotFoundException e) {
        return buildResponseEntity("An unexpected error occurred", HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessageDTO> handleValidationException(ValidationException e) {
        return buildResponseEntity("An unexpected error occurred", HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGenericException(Exception e) {
        return buildResponseEntity("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
}
