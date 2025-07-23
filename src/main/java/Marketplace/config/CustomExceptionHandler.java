package Marketplace.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import Marketplace.commons.ApplicationResponse;
import Marketplace.commons.ErrorMessage;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    public static final String ERROR_TYPE_SQL = "SQL";
    public static final String ERROR_TYPE_AUTH = "AUTH";
    public static final String ERROR_TYPE_MAIL = "MAIL";
    public static final String ERROR_TYPE_IO = "IO";

    @ExceptionHandler({ SQLException.class })
    public ResponseEntity<ApplicationResponse<Object>> handleSQLExceptions(SQLException ex, WebRequest request) {
        log.info("SQL Exception atrapada: " + ex.getMessage());

        ErrorMessage errorMessage = new ErrorMessage.Builder(
                Integer.toString(ex.getErrorCode()),
                ex.getMessage(),
                ERROR_TYPE_SQL).build();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            int codeValue = Integer.parseInt(errorMessage.getCode());
            if (codeValue > 30000) {
                status = HttpStatus.UNPROCESSABLE_ENTITY;
            }
        } catch (NumberFormatException e) {
            // si no es numérico, queda en 500
        }

        ApplicationResponse<Object> response = new ApplicationResponse<>(
                null,
                Collections.singletonList(errorMessage));

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<ApplicationResponse<Object>> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {

        // 1) Mensaje por defecto
        String userMsg;

        // 2) Si es BadCredentialsException, lo reemplazamos
        if ("Bad credentials".equals(ex.getMessage())) {
            userMsg = "Credenciales inválidas";
        } else {
            // 3) Para el resto, extraemos la causa raíz como antes
            Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
            userMsg = (root != null && root.getMessage() != null)
                    ? root.getMessage()
                    : ex.getMessage();
        }

        log.info("Authentication failed: {}", userMsg);

        ErrorMessage errorMessage = new ErrorMessage.Builder(
                "401",
                userMsg,
                ERROR_TYPE_AUTH).build();

        ApplicationResponse<Object> response = new ApplicationResponse<>(
                null,
                Collections.singletonList(errorMessage));

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler({ MessagingException.class })
    public ResponseEntity<ApplicationResponse<Object>> handleMessagingException(
            MessagingException ex,
            WebRequest request) {

        log.error("Error enviando e-mail: {}", ex.getMessage(), ex);

        ErrorMessage errorMessage = new ErrorMessage.Builder(
                "500", // no hay errorCode en MessagingException
                "Fallo al enviar el correo",
                ERROR_TYPE_MAIL)
                .build();

        ApplicationResponse<Object> response = new ApplicationResponse<>(
                null,
                Collections.singletonList(errorMessage));

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler({ IOException.class })
    public ResponseEntity<ApplicationResponse<Object>> handleIOException(
            IOException ex,
            WebRequest request) {

        log.error("IO Exception atrapada: {}", ex.getMessage(), ex);

        ErrorMessage errorMessage = new ErrorMessage.Builder(
                "500",
                ex.getMessage(),
                ERROR_TYPE_IO)
                .build();

        ApplicationResponse<Object> response = new ApplicationResponse<>(
                null,
                Collections.singletonList(errorMessage));

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
