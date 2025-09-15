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

    @Mapping(target = "emprestimos", ignore = true)
    LivroEntity toEntity(Livro livro);

    Livro entityToDomain(LivroEntity livroEntity);

    default Livro entityToDomainOptional(Optional<LivroEntity> livroEntity){
        return livroEntity.map(this::entityToDomain).orElse(null);
    }
}
