package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LivroMapper {

    @Mapping(target = "disponivel", ignore = true)
    @Mapping(target = "id", ignore = true)
    Livro toDomain(LivroRequestDTO livroRequestDTO);
    LivroResponseDTO toResponse(Livro livro);
}
