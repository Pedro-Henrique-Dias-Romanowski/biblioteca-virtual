package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.LivroValidate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroUseCase {

    private final LivroOutputPort livroOutputPort;

    private final LivroValidate livroValidate;

    public LivroUseCase(LivroOutputPort livroOutputPort, LivroValidate livroValidate) {
        this.livroOutputPort = livroOutputPort;
        this.livroValidate = livroValidate;
    }

    public Livro cadastrarLivro(Livro livro) throws DataBaseException {
        try{
            livroValidate.validarLivro(livro);
            return livroOutputPort.cadastrarLivro(livro);
        } catch (BusinessException e){
            throw new BusinessException(e.getMessage());
        }
    }

    public List<Livro> visualizarTodosOsLivros(){
        return livroOutputPort.visualizaTodosOsLivros();
    }

    public Optional<Livro> buscarLivroPorId(Long idLivro){
        return livroOutputPort.buscarLivroPorId(idLivro);
    }

    public void removerLivro(Long idLivro) throws DataBaseException {
        try{
            livroValidate.validarIdLivro(idLivro);
            livroOutputPort.removerLivro(idLivro);
        } catch (BusinessException e){
            throw new BusinessException(e.getMessage());
        }
    }
}
