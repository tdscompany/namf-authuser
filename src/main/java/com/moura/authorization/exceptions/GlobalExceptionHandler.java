package com.moura.authorization.exceptions;

import com.moura.authorization.utils.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

import static com.moura.authorization.utils.MessageUtils.messageSource;


@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException e, HttpServletRequest request) {

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .error("Unauthorized")
                .message(MessageUtils.get("error.invalid_credentials"))
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
