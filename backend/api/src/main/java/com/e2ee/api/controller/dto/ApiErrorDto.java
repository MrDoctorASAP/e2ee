package com.e2ee.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorDto {

    private int code;
    private String message;
    private Timestamp timestamp;

    public static ResponseEntity<ApiErrorDto> error(HttpStatus status, String message) {
        long time = System.currentTimeMillis();
        ApiErrorDto errorBody = new ApiErrorDto(status.value(), message, new Timestamp(time));
        return ResponseEntity.status(status).body(errorBody);
    }

    public static ResponseEntity<ApiErrorDto> exception(HttpStatus status, Exception e) {
        return error(status, e.getMessage());
    }

}
