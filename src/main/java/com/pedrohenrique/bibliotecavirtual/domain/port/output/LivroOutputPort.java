package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroOutputPort {

    Livro cadastrarLivro(Livro livro);
    List<Livro> visualizaTodosOsLivros();
    Optional<Livro> buscarLivroPorId(Long idLivro);
    void removerLivro(Long idLivro);
    boolean existeLivroPorId(Long idLivro);
    boolean existeLivroPorTitulo(String titulo);
}
