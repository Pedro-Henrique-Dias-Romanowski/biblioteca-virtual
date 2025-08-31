package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;

public record ClienteRequestDTO(
        Long id,
        String nome,
        String email,
        String senha,
        Integer qtLivrosEmprestados,
        Perfil perfil
) {
}
