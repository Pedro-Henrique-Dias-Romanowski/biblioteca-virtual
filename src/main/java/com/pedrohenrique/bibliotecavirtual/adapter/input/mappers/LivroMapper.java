package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LivroMapper {

    // todo verificar os dtos, pois para o mapper funcionar, os atributos devem ser iguais
    Livro toDomain(LivroRequestDTO livroRequestDTO);
    LivroResponseDTO toResponse(Livro livro);
}
