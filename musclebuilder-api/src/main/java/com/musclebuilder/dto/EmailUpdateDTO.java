package com.musclebuilder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailUpdateDTO (

    @NotBlank(message = "O e-mail não pode estar em branco")
    @Email(message = "E-mail deve ter formato válido")
    String newEmail,

    @NotBlank(message = "A senha é obrigatória")
    String currentPassword

) {}
