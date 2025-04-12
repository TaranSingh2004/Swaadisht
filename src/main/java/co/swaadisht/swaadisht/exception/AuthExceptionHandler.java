package co.swaadisht.swaadisht.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.sasl.AuthenticationException;
// other imports...

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler({IndexOutOfBoundsException.class, AuthenticationException.class})
    public ResponseEntity<String> handleAuthException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed: " + ex.getMessage());
    }
}