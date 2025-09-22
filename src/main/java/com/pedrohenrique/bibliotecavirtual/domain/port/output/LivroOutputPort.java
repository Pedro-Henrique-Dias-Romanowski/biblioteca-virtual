package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroOutputPort {

    Livro cadastrarLivro(Livro livro);
    List<Livro> visualizaTodosOsLivros();
    Optional<Livro> buscarLivroPorId(Long idLivro);
    void removerLivro(Long idLivro);
    boolean existsById(Long idLivro);
    boolean existsByTitulo(String titulo);
    Livro pegarReferenciaPorId(Long idLivro);
}
