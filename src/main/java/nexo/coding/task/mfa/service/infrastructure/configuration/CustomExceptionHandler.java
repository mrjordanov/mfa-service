package nexo.coding.task.mfa.service.infrastructure.configuration;

import lombok.extern.slf4j.Slf4j;
import nexo.coding.task.mfa.service.infrastructure.exceptions.CodedException;
import nexo.coding.task.mfa.service.infrastructure.exceptions.ErrorCode;
import nexo.coding.task.mfa.service.responses.ResponseDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

import static nexo.coding.task.mfa.service.responses.ResponseDto.fail;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CodedException.class)
    public ResponseEntity<ResponseDto<Void>> handleCodedException(CodedException ex) {
        log.error(ex.getMessage(), ex);
        return getResponse(HttpStatus.BAD_REQUEST, fail(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error(ErrorCode.VALIDATION_ERROR.toMessage(message));

        return getResponse(HttpStatus.BAD_REQUEST, fail(ErrorCode.VALIDATION_ERROR.getCode(), ErrorCode.VALIDATION_ERROR.toMessage(message)));
    }

    private ResponseEntity<ResponseDto<Void>> getResponse(HttpStatusCode status, ResponseDto<Void> content) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(content);
    }
}