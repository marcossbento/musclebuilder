package com.musclebuilder.dto;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken
) {
}
