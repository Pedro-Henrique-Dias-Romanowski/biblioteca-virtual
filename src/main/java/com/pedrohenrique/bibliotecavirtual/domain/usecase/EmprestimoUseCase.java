package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.EmprestimoValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoUseCase {

    private final EmprestimoValidate emprestimoValidate;

    private final EmprestimoOutputPort emprestimoOutputPort;

    private final LivroOutputPort livroOutputPort;
    private final ClienteOutputPort clienteOutputPort;

    @Value("${mensagem.erro.livro.indisponivel}")
    private String mensagemErroLivroIndisponivel;


    private final Logger logger = LoggerFactory.getLogger(EmprestimoUseCase.class);

    public EmprestimoUseCase(EmprestimoValidate emprestimoValidate, EmprestimoOutputPort emprestimoOutputPort, LivroOutputPort livroOutputPort, ClienteOutputPort clienteOutputPort) {
        this.emprestimoValidate = emprestimoValidate;
        this.emprestimoOutputPort = emprestimoOutputPort;
        this.livroOutputPort = livroOutputPort;
        this.clienteOutputPort = clienteOutputPort;
    }


    public Emprestimo realizarEmprestimo(Emprestimo emprestimo) {
        try{
            emprestimo.setDataEmprestimo(LocalDate.now());
            emprestimo.setAtivo(true);
            emprestimoValidate.validarEmprestimo(emprestimo);
            atualizarDisponibilidadeDosLivrosAposEmprestimo(emprestimo);
            logger.info("Inciando processo de empréstimo");
            return emprestimoOutputPort.realizarEmprestimo(emprestimo);
        } catch (BusinessException e){
            logger.error("Erro ao realizar emprestimo: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    public Emprestimo realizarDevolucaoEmprestimo(Emprestimo emprestimo){
        try{
            emprestimoValidate.validarDevolucao(emprestimo);
            var emprestimoDevolucao = emprestimoOutputPort.getReferenceById(emprestimo.getId());
            if (emprestimoDevolucao == null) {
                throw new BusinessException("Empréstimo não encontrado");
            }
            emprestimoDevolucao.setAtivo(false);
            emprestimoDevolucao.setDataDevolucao(LocalDate.now());
            atualizarDisponibilidadeDosLivrosAposDevolucao(emprestimoDevolucao);
            logger.info("Iniciando processo de devolução de empréstimo");
            return emprestimoOutputPort.realizarDevolucaoEmprestimo(emprestimoDevolucao);
        } catch (BusinessException e){
            logger.error("Erro ao realizar devolução de empréstimo: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        } catch (NullPointerException e) {
            logger.error("Erro ao realizar devolução de empréstimo: Empréstimo não encontrado");
            throw new BusinessException("Empréstimo não encontrado");
        }
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(Long idCliente){
        logger.info("Buscando todos os emprestimos");
        return emprestimoOutputPort.visualizarTodosOsEmprestimos(idCliente);
    }


    private void atualizarDisponibilidadeDosLivrosAposEmprestimo(Emprestimo emprestimo){
        for (Long idLivro : emprestimo.getLivros()){
            var livro = livroOutputPort.buscarLivroPorId(idLivro);
            livro.get().setDisponivel(false);
        }
    }

    private void atualizarDisponibilidadeDosLivrosAposDevolucao(Emprestimo emprestimo){
        for (Long idLivro : emprestimo.getLivros()){
            var livro = livroOutputPort.buscarLivroPorId(idLivro);
            livro.get().setDisponivel(true);
        }
    }
}
