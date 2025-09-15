package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroUseCase {

    private final LivroOutputPort livroOutputPort;

    public LivroUseCase(LivroOutputPort livroOutputPort) {
        this.livroOutputPort = livroOutputPort;
    }

    public Livro cadastrarLivro(Livro livro) throws DataBaseException {
        if (livro != null && !livroOutputPort.existeLivroPorTitulo(livro.getTitulo())){
            return livroOutputPort.cadastrarLivro(livro);
        } else {
            throw new DataBaseException("Livro nulo ou já existente");
        }
    }

    public List<Livro> visualizarTodosOsLivros(){
        return livroOutputPort.visualizaTodosOsLivros();
    }

    public Optional<Livro> buscarLivroPorId(Long idLivro){
        if (idLivro != null && livroOutputPort.existeLivroPorId(idLivro)){
            return livroOutputPort.buscarLivroPorId(idLivro);
        } else {
            return Optional.empty();
        }
    }


    public void removerLivro(Long idLivro) throws DataBaseException {
        if (idLivro != null && livroOutputPort.existeLivroPorId(idLivro)){
            livroOutputPort.removerLivro(idLivro);
        } else {
            throw new DataBaseException("ID do livro nulo ou inexistente");
        }
    }
}
