package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface LivroMapper {

    @Mapping(target = "disponivel", ignore = true)
    @Mapping(target = "id", ignore = true)
    Livro toDomain(LivroRequestDTO livroRequestDTO);
    LivroResponseDTO toResponse(Livro livro);

    LivroEntity toEntity(Livro livro);
    Livro entityToDomain(LivroEntity livroEntity);
    Livro entityToDomainOptional(Optional<LivroEntity> livroEntity);
}
