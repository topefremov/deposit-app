package ru.alexefremov.depositapp.depositservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.alexefremov.depositapp.depositservice.dto.ErrorDto;
import ru.alexefremov.depositapp.depositservice.exception.BusinessLayerException;

@ControllerAdvice
@Slf4j
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(BusinessLayerException.class)
    public ErrorDto handleBusinessLayerException(BusinessLayerException e) {
            log.warn(e.getMessage());
            return ErrorDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .description(e.getMessage())
                .build();
    }
}
