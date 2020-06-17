package br.com.netshoes.cep.interceptors;

import br.com.netshoes.cep.presenters.ErrorPresenter;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import static br.com.netshoes.cep.constants.Constants.ZIP_POSTAL_CODE_INVALID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ApiIgnore
@ControllerAdvice
public class HandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorPresenter> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        final var result = ex.getBindingResult();
        final var errorPresenter = getError(result.getFieldErrors().get(0).getDefaultMessage(), BAD_REQUEST);
        return ResponseEntity.status(BAD_REQUEST)
                .body(errorPresenter);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorPresenter> handleResponseStatusException(final ResponseStatusException ex) {
        final var errorPresenter = getError(ex.getReason(), ex.getStatus());
        return ResponseEntity.status(ex.getStatus())
                .body(errorPresenter);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorPresenter> handleJsonParseException(final JsonParseException ex) {
        final var errorPresenter = getError(ZIP_POSTAL_CODE_INVALID, BAD_REQUEST);
        return ResponseEntity.status(BAD_REQUEST)
                .body(errorPresenter);
    }

    private ErrorPresenter getError(final String message, final HttpStatus status) {
        return ErrorPresenter.builder()
                .message(message)
                .status(status)
                .build();
    }
}