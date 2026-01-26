package com.musclebuilder.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
                int statusCode,
                Map<String, String> validationErrors,
                String path,
                LocalDateTime timestamp) {
}
