package com.musclebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordUpdateDTO (

    @NotBlank(message = "A senha atual é obrigatória")
    String currentPassword,

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres")
    String newPassword,

    @NotBlank(message = "A confirmação de senha é obrigatória")
    String confirmPassword

) {}