package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.ClienteRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.ClienteResponseDTO;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    // todo verificar os dtos,pois para o mapper funcionar,os atributos devem ser iguais
    Cliente toDomain(ClienteRequestDTO clienteResponseDTO);
    ClienteResponseDTO toResponse(Cliente cliente);
}
