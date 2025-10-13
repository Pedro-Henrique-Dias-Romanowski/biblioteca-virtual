package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaRequestDTO(

        @NotBlank
        @Email
        String email,

        @NotBlank
        Integer codigo,

        @NotBlank
        String novaSenha,

        @NotBlank
        String confirmacacaoNovaSenha
) {}
