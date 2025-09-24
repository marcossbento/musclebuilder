package com.musclebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "O refresh token não pode estar em branco")
        String token
) {}
