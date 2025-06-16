package com.musclebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record UserRegistrationDTO (

    @NotBlank(message = "Nome é obrigatório")
    String name,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha é obrigatório")
    @Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
    String password,

    String height,

    String weight,

    String goal
) {}