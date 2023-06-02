package cpu.spec.dataset.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.logging.Logger;

// https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
    private final boolean printStackTrace;

    public GlobalExceptionHandler(@Value("${logging.level.root}") String logLevel) {
        this.printStackTrace = logLevel.equalsIgnoreCase("DEBUG");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> handleNoSuchElementException(HttpServletRequest request, NoSuchElementException exception) {
        LOGGER.warning(exception.getClass().getSimpleName() + ": " + exception.getMessage() + " (from: " + request.getRequestURI() + ")");
        logHandledException(request, exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private void logHandledException(HttpServletRequest request, Exception exception) {
        if (printStackTrace) {
            exception.printStackTrace();
        }
    }
}
