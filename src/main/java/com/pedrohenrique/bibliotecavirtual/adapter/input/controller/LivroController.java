package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.LivroControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class LivroController implements LivroControllerSwagger {
    @Override
    public ResponseEntity<LivroResponseDTO> cadastrarLivro(LivroRequestDTO livroRequestDTO, String tokenJwt) {
        return null;
    }

    @Override
    public ResponseEntity<LivroResponseDTO> atualizarLivro(Long idLivroAntigo, LivroRequestDTO livroRequestDTO, String tokenJwt) {
        return null;
    }

    @Override
    public ResponseEntity<List<LivroResponseDTO>> visualizarTodosOsLivros(String tokenJwt) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<LivroResponseDTO>> buscarLivro(Long idLivro, String tokenJwt) {
        return null;
    }

    @Override
    public ResponseEntity<Void> removerLivro(Long idLivro, String tokenJwt) {
        return null;
    }
}
