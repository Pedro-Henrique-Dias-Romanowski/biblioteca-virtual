package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNaoEcontradoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNuloException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LivroValidate {

    private final LivroOutputPort livroOutputPort;

    @Value("${mensagem.erro.livro.nulo}")
    private String mensagemErroLivroNulo;

    @Value("${mensagem.erro.livro.nao.encontrado}")
    private String mensagemErroLivroNaoEcontrado;

    public LivroValidate(LivroOutputPort livroOutputPort) {
        this.livroOutputPort = livroOutputPort;
    }

    public void validarLivro(Livro livro){
        validarNulidade(livro);
        validarExistenciaLivro(livro);
    }

    public void validarIdLivro(Long id){
        validarNulidadeIdLivro(id);
        validarExistenciaIdLivro(id);
    }

    private void validarNulidade(Livro livro){
        if (livro == null) throw new LivroNuloException(mensagemErroLivroNulo);
    }

    private void validarExistenciaLivro(Livro livro){
        if (!livroOutputPort.existsByTitulo(livro.getTitulo()))
            throw new LivroNaoEcontradoException(mensagemErroLivroNaoEcontrado + livro.getTitulo());
    }

    private void  validarNulidadeIdLivro(Long id){
        if (id == null) throw  new LivroNuloException(mensagemErroLivroNulo + "ID: " + id);
    }

    private void validarExistenciaIdLivro(Long id){
        if (livroOutputPort.existsById(id)) throw new LivroNaoEcontradoException(mensagemErroLivroNaoEcontrado + id);
    }
}
