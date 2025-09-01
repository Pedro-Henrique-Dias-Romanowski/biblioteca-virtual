package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {

    // todo verificar os dtos, pois para o mapper funcionar, os atributos devem ser iguais
    Emprestimo toDomain(EmprestimoRequestDTO emprestimoRequestDTO);
    EmprestimoResponseDTO toResponse(Emprestimo emprestimo);
}
