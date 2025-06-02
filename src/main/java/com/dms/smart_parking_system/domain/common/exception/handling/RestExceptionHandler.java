package com.dms.smart_parking_system.domain.common.exception.handling;

import com.dms.smart_parking_system.domain.common.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(CommonException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage(
                ex.getErrorCode().name(),
                ex.getParams().toArray(),
                locale
        );

        ErrorResponse body = ErrorResponse.builder()
                .message(msg)
                .errorCode(ex.getErrorCode())
                .status(ex.getStatus())
                .build();

        return new ResponseEntity<>(body, ex.getStatus());
    }

}