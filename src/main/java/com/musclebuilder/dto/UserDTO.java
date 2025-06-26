package com.musclebuilder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record UserDTO (
    Long id,

    @NotBlank(message = "Nome é obrigatório")
     String name,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de e-mail inválido")
     String email,

    String height,
    String weight,
    String goal
) {}