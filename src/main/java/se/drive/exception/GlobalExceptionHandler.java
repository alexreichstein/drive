package se.drive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global felhanterare för alla controllers.
 * Fångar upp exceptions och returnerar strukturerade felmeddelanden.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Hanterar ResourceNotFoundException (404).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex
    ) {
        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Hanterar IOException (500) - t.ex. när fil inte kan läsas/skrivas.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(
            IOException ex
    ) {
        return buildErrorResponse(
                "Filhanteringsfel: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Hanterar för stora filer (413).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(
            MaxUploadSizeExceededException ex
    ) {
        return buildErrorResponse(
                "Filen är för stor för att laddas upp",
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    /**
     * Hanterar generella exceptions (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex
    ) {
        return buildErrorResponse(
                "Ett oväntat fel uppstod: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Bygger ett felmeddelande.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message,
            HttpStatus status
    ) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);

        return new ResponseEntity<>(error, status);
    }
}