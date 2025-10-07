package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequestDTO(

        @NotBlank
        @Valid
        String nome,
        @NotBlank
        @Valid
        @Email
        String email,
        @NotBlank
        @Valid
        String senha,
        Integer qtLivrosEmprestados
) {
}
