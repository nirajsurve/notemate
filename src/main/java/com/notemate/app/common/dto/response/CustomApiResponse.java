package com.notemate.app.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomApiResponse<T> {
    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    private T data;
    private String error;

    public static <T> CustomApiResponse<T> success(String message, T data) {
        return CustomApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .data(data)
                .error(null)
                .build();
    }

    public static <T> CustomApiResponse<T> error(String error) {
        return CustomApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message("Error occurred!")
                .error(error)
                .build();
    }
}
