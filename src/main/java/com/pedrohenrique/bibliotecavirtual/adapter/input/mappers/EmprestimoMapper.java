package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.EmprestimoEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {


    @Mapping(target = "id", ignore = true)
    Emprestimo toDomain(EmprestimoRequestDTO emprestimoRequestDTO);

    @Mapping(target = "idEmprestimo", source = "id")
    EmprestimoResponseDTO toResponse(Emprestimo emprestimo);

    @Mapping(target = "clienteId", source = "clienteId", qualifiedByName = "mapCliente")
    @Mapping(target = "livros", source = "livros", qualifiedByName = "mapLivro")
    EmprestimoEntity toEntity(Emprestimo emprestimo);

    @Named("mapCliente")
    default ClienteEntity mapCliente(Long id) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(id);
        return cliente;
    }

    @Named("mapLivro")
    default List<LivroEntity> mapLivro(List<Long> ids) {
        return ids.stream().map(id -> {
            LivroEntity livro = new LivroEntity();
            livro.setId(id);
            return livro;
        }).toList();
    }

    @Named("mapLivroDomain")
    default List<Long> mapLivroDomain(List<LivroEntity> ids) {
        return ids.stream().map(id -> {
            LivroEntity livro = new LivroEntity();
            return livro.getId();
        }).toList();
    }

    @Mapping(target = "clienteId", source = "clienteId.id")
    @Mapping(target = "livros", source = "livros", qualifiedByName = "mapLivroDomain")
    Emprestimo entityToDomain(EmprestimoEntity entity);

}
