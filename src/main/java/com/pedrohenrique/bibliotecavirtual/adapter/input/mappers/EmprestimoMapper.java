package com.pedrohenrique.bibliotecavirtual.adapter.input.mappers;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.EmprestimoEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.LivroRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNaoEcontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EmprestimoMapper {

    private final ClienteRepository clienteRepository;

    private final LivroRepository livroRepository;

    @Value("${mensagem.erro.dados.cliente.id.nao.encontrado}")
    private String mensagemErroDadosClienteIdNaoEncontrado;

    @Value("${mensagem.erro.livro.indisponivel}")
    private String mensagemErroLivroIndisponivel;


    public EmprestimoMapper(ClienteRepository clienteRepository, LivroRepository livroRepository) {
        this.clienteRepository = clienteRepository;
        this.livroRepository = livroRepository;
    }

    public Emprestimo toDomain(EmprestimoRequestDTO emprestimoRequestDTO){
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setClienteId(emprestimoRequestDTO.clienteId());
        emprestimo.setLivros(emprestimoRequestDTO.livros());
        emprestimo.setDataDevolucao(emprestimoRequestDTO.dataDevolucao());

        return emprestimo;
    }

    public EmprestimoResponseDTO toResponse(Emprestimo emprestimo){
        return new EmprestimoResponseDTO(
                emprestimo.getId(),
                emprestimo.getClienteId(),
                emprestimo.getLivros(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataDevolucao(),
                emprestimo.getAtivo()
        );
    }

    public EmprestimoEntity toEntity(Emprestimo emprestimo){
        EmprestimoEntity emprestimoEntity = new EmprestimoEntity();
        emprestimoEntity.setId(emprestimo.getId());
        emprestimoEntity.setClienteId(pegarReferenciaCliente(emprestimo.getClienteId()));
        emprestimoEntity.setLivros(pegarReferenciaLivros(emprestimo.getLivros()));
        emprestimoEntity.setDataEmprestimo(emprestimo.getDataEmprestimo());
        emprestimoEntity.setDataDevolucao(emprestimo.getDataDevolucao());

        return emprestimoEntity;
    }

    public Emprestimo entityToDomain(EmprestimoEntity emprestimoEntity){
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(emprestimoEntity.getId());
        emprestimo.setClienteId(emprestimoEntity.getClienteId() != null ? emprestimoEntity.getClienteId().getId() : null);
        emprestimo.setLivros(pegarIdsLivros(emprestimoEntity.getLivros()));
        emprestimo.setDataEmprestimo(emprestimoEntity.getDataEmprestimo());
        emprestimo.setDataDevolucao(emprestimoEntity.getDataDevolucao());

        return emprestimo;
    }

    private List<Long> pegarIdsLivros(List<LivroEntity> livros){
        if (livros == null){
            return List.of();

        }
        return livros.stream()
                .filter( l -> l != null && l.getId() != null)
                .map(LivroEntity::getId)
                .toList();
    }

    private ClienteEntity pegarReferenciaCliente(Long clienteId){
        return clienteRepository.findById(clienteId).orElseThrow(() -> new ClienteInvalidoException(mensagemErroDadosClienteIdNaoEncontrado + clienteId));
    }

    private List<LivroEntity> pegarReferenciaLivros(List<Long> livros){
        if (livros == null || livros.isEmpty()) {
            return List.of();
        }

        return livros.stream().map(
                id -> livroRepository.findById(id).orElseThrow(() -> new LivroNaoEcontradoException(mensagemErroLivroIndisponivel + id)))
                .filter(Objects::nonNull)
                .toList();

    }


}
