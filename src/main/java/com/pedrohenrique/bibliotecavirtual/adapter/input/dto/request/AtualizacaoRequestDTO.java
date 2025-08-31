package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;

public record AtualizacaoRequestDTO(
        Long idLivroAntigo,
        Livro livro) {
}
