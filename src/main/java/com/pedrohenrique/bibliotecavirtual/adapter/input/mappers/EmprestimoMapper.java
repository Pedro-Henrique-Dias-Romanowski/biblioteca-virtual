package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {


    @Mapping(target = "id", ignore = true)
    Emprestimo toDomain(EmprestimoRequestDTO emprestimoRequestDTO);

    @Mapping(target = "idEmprestimo", source = "id")
    EmprestimoResponseDTO toResponse(Emprestimo emprestimo);
}
