package ru.alexefremov.depositapp.depositservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.alexefremov.depositapp.depositservice.dto.ErrorDto;
import ru.alexefremov.depositapp.depositservice.exception.BusinessLayerException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@ParametersAreNonnullByDefault
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(BusinessLayerException.class)
    public ErrorDto handleBusinessLayerException(BusinessLayerException e, WebRequest webRequest) {
        log.warn("Request {}. Error message: {}", webRequest, e.getMessage());
        return ErrorDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .description(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorDto handleAccessDeniedException(AccessDeniedException e, WebRequest webRequest) {
        log.warn("Request {}. Error message: {}", webRequest, e.getMessage());
        return ErrorDto.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .description("Access denied")
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ErrorDto handleAuthenticationException(AuthenticationException e, WebRequest webRequest) {
        log.warn("Request {}. Error message: {}", webRequest, e.getMessage());
        return ErrorDto.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .description("Unauthorized access")
                .build();
    }

    @Override
    @Nonnull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.warn("Request {}. Error message: {}", request, e.getMessage());
        List<String> details = e.getFieldErrors().stream()
                .map(fe -> String.format("Rejected field: %s. Rejected value: %s. Reason: %s", fe.getField(),
                        fe.getRejectedValue(), fe.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(ErrorDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .description("Wrong input")
                .details(details)
                .build());
    }
}
