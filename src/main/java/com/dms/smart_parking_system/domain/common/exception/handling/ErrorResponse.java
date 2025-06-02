package com.dms.smart_parking_system.domain.common.exception.handling;

import com.dms.smart_parking_system.domain.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private HttpStatus status;
    private String message;
}
