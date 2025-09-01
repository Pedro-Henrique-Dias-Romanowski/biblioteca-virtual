package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response;

public record LivroResponseDTO(
        String titulo,
        String autor,
        String editora,
        Integer anoPublicacao,
        Boolean disponivel
) {
}
