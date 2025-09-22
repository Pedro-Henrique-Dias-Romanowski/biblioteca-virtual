package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.LivroControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.LivroMapper;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.LivroUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class LivroController implements LivroControllerSwagger {

    private final LivroMapper livroMapper;
    private final LivroUseCase livroUseCase;

    public LivroController(LivroMapper livroMapper, LivroUseCase livroUseCase) {
        this.livroMapper = livroMapper;
        this.livroUseCase = livroUseCase;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LivroResponseDTO> cadastrarLivro(LivroRequestDTO livroRequestDTO) throws DataBaseException {
        var livro = livroMapper.toDomain(livroRequestDTO);
        var livroSalvo = livroUseCase.cadastrarLivro(livro);

        return ResponseEntity.ok().body(livroMapper.toResponse(livroSalvo));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<LivroResponseDTO>> visualizarTodosOsLivros() {
        var livros = livroUseCase.visualizarTodosOsLivros()
                .stream()
                .map(livroMapper::toResponse)
                .toList();

        return ResponseEntity.ok().body(livros);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Optional<LivroResponseDTO>> buscarLivroPorId(Long idLivro){
        var livro = livroUseCase.buscarLivroPorId(idLivro);
        return livro.isPresent() ? ResponseEntity.ok().body(livro.map(livroMapper::toResponse)) :  ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removerLivro(Long idLivro) throws Exception, BusinessException {
        livroUseCase.removerLivro(idLivro);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
