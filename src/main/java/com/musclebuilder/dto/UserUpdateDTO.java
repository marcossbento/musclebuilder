package com.musclebuilder.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserUpdateDTO(
        @NotEmpty(message = "O nome não pode estar em branco")
        String name,

        String height,

        String weight,

        String goal
) {}
