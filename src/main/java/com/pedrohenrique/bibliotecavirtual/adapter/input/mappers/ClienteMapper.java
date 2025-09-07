package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.ClienteRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.ClienteResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "perfil", ignore = true)
    Cliente toDomain(ClienteRequestDTO clienteResponseDTO);

    ClienteResponseDTO toResponse(Cliente cliente);

    @Mapping(target = "emprestimos", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    ClienteEntity toEntity(Cliente cliente);

    @Mapping(target = "qtLivrosEmprestados", expression = "java(clienteEntity.getEmprestimos() != null ? clienteEntity.getEmprestimos().size() : 0)")
    Cliente entityToDomain(ClienteEntity clienteEntity);


}
