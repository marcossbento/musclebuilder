package com.musclebuilder.dto;

public record AuthenticationResponse(
        String token,
        String refreshToken
) {
}
