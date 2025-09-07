package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.LivroControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public class LivroController implements LivroControllerSwagger {

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LivroResponseDTO> cadastrarLivro(LivroRequestDTO livroRequestDTO, String tokenJwt) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LivroResponseDTO> atualizarLivro(Long idLivroAntigo, LivroRequestDTO livroRequestDTO, String tokenJwt) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<LivroResponseDTO>> visualizarTodosOsLivros(String tokenJwt) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Optional<LivroResponseDTO>> buscarLivroPorId(Long idLivro, String tokenJwt) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removerLivro(Long idLivro, String tokenJwt) {
        return null;
    }
}
