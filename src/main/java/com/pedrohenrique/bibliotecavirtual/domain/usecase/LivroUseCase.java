package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.LivroValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroUseCase {

    private final LivroOutputPort livroOutputPort;

    private final LivroValidate livroValidate;

    private final Logger logger = LoggerFactory.getLogger(LivroUseCase.class);

    public LivroUseCase(LivroOutputPort livroOutputPort, LivroValidate livroValidate) {
        this.livroOutputPort = livroOutputPort;
        this.livroValidate = livroValidate;
    }

    public Livro cadastrarLivro(Livro livro) {
        try{
            livro.setDisponivel(true);
            livroValidate.validarCadastroLivro(livro);
            logger.info("Livro do id {} cadastrado com sucesso", livro.getId());
            return livroOutputPort.cadastrarLivro(livro);
        } catch (BusinessException e){
            logger.error("Erro ao cadastrar livro: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    public List<Livro> visualizarTodosOsLivros(){
        logger.info("Buscando todos os livros");
        return livroOutputPort.visualizaTodosOsLivros();
    }

    public Optional<Livro> buscarLivroPorId(Long idLivro){
        logger.info("Buscando livro do ID {}", idLivro);
        return livroOutputPort.buscarLivroPorId(idLivro);
    }

    public void removerLivro(Long idLivro) {
        try{
            livroValidate.validarIdLivro(idLivro);
            logger.info("Livro do ID {} removido com sucesso", idLivro);
            livroOutputPort.removerLivro(idLivro);
        } catch (BusinessException e){
            logger.error("Erro ao remover livro: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }
}
