package com.musclebuilder.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int statusCode,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
