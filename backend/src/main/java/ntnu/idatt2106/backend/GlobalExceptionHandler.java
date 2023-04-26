package ntnu.idatt2106.backend;

import ntnu.idatt2106.backend.exceptions.*;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the backend API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<String> handleFileSizeLimitExceededException(FileSizeLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The file size limit has been exceeded: " + ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ShoppingListNotFound.class)
    public ResponseEntity<String> handleShoppingListNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ShoppingCartNotFound.class)
    public ResponseEntity<String> handleShoppingCartNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(SubCategoryNotFound.class)
    public ResponseEntity<String> handleSubCategoryNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(SaveException.class)
    public ResponseEntity<String> handleSaveException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RefrigeratorNotFoundException.class)
    public ResponseEntity<String> handleRefrigeratorNotFoundException(RefrigeratorNotFoundException ex) {
        logger.warn("RefrigeratorNotFoundException thrown: " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OldPasswordDoesNotMatchException.class)
    public ResponseEntity<String> handleOldPasswordDoesNotMatchException(OldPasswordDoesNotMatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        logger.info("An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<String> handleNotificationException(NotificationException ex){
        logger.info("An error uccured when retrieving notifications" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred when retrieving notifications: " + ex.getMessage());
    }
}
