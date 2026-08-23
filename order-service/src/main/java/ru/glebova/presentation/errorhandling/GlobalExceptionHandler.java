package ru.glebova.presentation.errorhandling;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.glebova.exceptions.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DomainValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDomainValidationException(DomainValidationException ex) {
        return new ErrorDto(ErrorTypeDto.DOMAIN_VALIDATION, ex.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        return new ErrorDto(ErrorTypeDto.ENTITY_ALREADY_EXISTS, ex.getMessage());
    }

    @ExceptionHandler(EntityInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleEntityInUseException(EntityInUseException ex) {
        return new ErrorDto(ErrorTypeDto.ENTITY_IN_USE, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ErrorDto(ErrorTypeDto.ENTITY_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IncompatibleComponentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ErrorDto handleIncompatibleComponentException(IncompatibleComponentException ex) {
        return new ErrorDto(ErrorTypeDto.INCOMPATIBLE_COMPONENT, ex.getMessage());
    }

    @ExceptionHandler(NotEnoughRightsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handleNotEnoughRightsException(NotEnoughRightsException ex) {
        return new ErrorDto(ErrorTypeDto.NOT_ENOUGH_RIGHTS, ex.getMessage());
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorDto> handleStatusRuntimeException(StatusRuntimeException ex) {
        var code = ex.getStatus().getCode();
        var message = ex.getStatus().getDescription();

        if (code == Status.Code.UNAVAILABLE || code == Status.Code.DEADLINE_EXCEEDED)
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorDto(ErrorTypeDto.SERVICE_UNAVAILABLE, message));

        if (code == Status.Code.INVALID_ARGUMENT)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDto(ErrorTypeDto.SERVICE_INVALID_ARGUMENT, message));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(ErrorTypeDto.SERVICE_INTERNAL, message));
    }
}
