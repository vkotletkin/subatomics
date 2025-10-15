package ru.kotletkin.subatomics.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kotletkin.subatomics.common.exception.dto.ErrorResponse;
import ru.kotletkin.subatomics.common.exception.dto.ValidationErrorResponse;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error("Элемент не найден. {}", e.getMessage());
        return new ErrorResponse("Элемент не найден", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleModuleExistException(ModuleExistException e) {
        log.error("Ошибка существования модуля. {}", e.getMessage());
        return new ErrorResponse("Ошибка существования модуля", e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ValidationErrorResponse handleIncorrectModulesException(IncorrectModulesException e) {
        log.error("Ошибка при заполнении полей связанных с модулями. {}", e.getMessage());
        return new ValidationErrorResponse(List.of(new ErrorResponse("Ошибка при заполнении полей связанных с модулями", e.getMessage())));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ValidationErrorResponse handleOnConstraintValidationException(ConstraintViolationException e) {

        final List<ErrorResponse> errorResponses = e.getConstraintViolations().stream()
                .map(error -> new ErrorResponse(
                                error.getPropertyPath().toString(),
                                error.getMessage()
                        )
                )
                .toList();

        log.error(e.getMessage());

        return new ValidationErrorResponse(errorResponses);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ValidationErrorResponse handleOnMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        final List<ErrorResponse> errorResponses = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        log.error(e.getMessage());

        return new ValidationErrorResponse(errorResponses);
    }
}
