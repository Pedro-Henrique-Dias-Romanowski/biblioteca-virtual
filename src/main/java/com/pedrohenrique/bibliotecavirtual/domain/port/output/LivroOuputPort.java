package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroOuputPort {

    Livro cadastrarLivro(Livro livro);
    Livro editarDadosLivro(Long idLivroAntigo, Livro livroAtualizado);
    List<Livro> visualizaTodosOsLivros();
    Optional<Livro> buscarLivroPorId(Livro livro);
    void removerLivro(Long idLivro);
}
