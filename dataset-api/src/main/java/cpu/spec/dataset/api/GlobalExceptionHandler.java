package cpu.spec.dataset.api;

import cpu.spec.dataset.api.exception.NonUniqueResultException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
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
        logHandledException(request, exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<List<Link>> handleNonUniqueResultException(HttpServletRequest request, NonUniqueResultException exception) {
        List<Link> links = new ArrayList<>();
        for (String result: exception.getResults()) {
            links.add(new Link("/api/cpu-dataset/equals/" + result));
        }
        logHandledException(request, exception);
        return ResponseEntity.status(HttpStatus.MULTIPLE_CHOICES).body(links);
    }

    private void logHandledException(HttpServletRequest request, Exception exception) {
        if (printStackTrace) {
            LOGGER.warning(exception.getClass().getSimpleName() + ": " + exception.getMessage() + " (from: " + request.getRequestURI() + ")");
            exception.printStackTrace();
        }
    }
}
