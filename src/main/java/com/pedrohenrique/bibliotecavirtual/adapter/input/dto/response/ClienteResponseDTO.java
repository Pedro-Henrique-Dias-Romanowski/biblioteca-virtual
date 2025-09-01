package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        Integer qtLivrosEmprestados
) {
}
