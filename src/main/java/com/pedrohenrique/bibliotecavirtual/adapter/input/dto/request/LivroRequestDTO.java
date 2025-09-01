package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record LivroRequestDTO(

        @NotBlank
        @Valid
        String titulo,
        @NotBlank
        @Valid
        String autor,
        @NotBlank
        @Valid
        String editora,
        @NotBlank
        @Valid
        Integer anoPublicacao
) {
}
