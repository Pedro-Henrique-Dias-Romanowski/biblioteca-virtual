package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EsqueciMinhaSenhaRequestDTO(
        @NotBlank
        @Email
        String email
){}
