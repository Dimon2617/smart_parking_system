package com.dms.smart_parking_system.domain.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommonException extends RuntimeException {

    private ErrorCode errorCode;
    private List<String> params;
    private HttpStatus status;

    public CommonException(ErrorCode errorCode, HttpStatus status) {
        this.errorCode = errorCode;
        this.status = status;
        this.params = List.of();
    }

    public CommonException(ErrorCode errorCode, HttpStatus status, List<String> params) {
        this.errorCode = errorCode;
        this.status = status;
        this.params = params;
    }

}