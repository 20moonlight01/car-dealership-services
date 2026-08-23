package ru.glebova.presentation.errorhandling;

import org.springframework.http.HttpStatus;
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
}
