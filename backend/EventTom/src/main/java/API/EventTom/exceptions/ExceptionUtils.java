package API.EventTom.exceptions;

import API.EventTom.dto.response.ErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ExceptionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

    public static ResponseEntity<ErrorMessageDTO> buildResponseEntity(String message, HttpStatus status, Exception e) {
        ErrorMessageDTO errorResponse = new ErrorMessageDTO(
                e.getMessage(),
                message,
                new Date(),
                status.value()
        );
        logger.error("Error: {}",  e.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
}
