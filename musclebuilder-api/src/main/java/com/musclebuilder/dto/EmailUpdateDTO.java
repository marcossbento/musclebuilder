package com.musclebuilder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailUpdateDTO {

    @NotBlank(message = "O e-mail não pode estar em branco")
    @Email(message = "E-mail deve ter formato válido")
    private String newEmail;

    @NotBlank(message = "A senha é obrigatória")
    private String currentPassword;

}
