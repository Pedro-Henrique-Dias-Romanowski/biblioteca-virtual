package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
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

    private final Logger logger = LoggerFactory.getLogger(EmprestimoUseCase.class);

    public EmprestimoUseCase(EmprestimoValidate emprestimoValidate, EmprestimoOutputPort emprestimoOutputPort) {
        this.emprestimoValidate = emprestimoValidate;
        this.emprestimoOutputPort = emprestimoOutputPort;
    }

    public Emprestimo realizarEmprestimo(Emprestimo emprestimo) {
        try{
            emprestimo.setDataEmprestimo(LocalDate.now());
            emprestimoValidate.validarEmprestimo(emprestimo);
            logger.info("O emprestimo foi confirmado com sucesso: ID: {}", emprestimo.getId());
            return emprestimoOutputPort.realizarEmprestimo(emprestimo);
        } catch (BusinessException e){
            logger.error("Erro ao realizar emprestimo: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(){
        logger.info("Buscando todos os emprestimos");
        return emprestimoOutputPort.visualizarTodosOsEmprestimos();
    }
}
