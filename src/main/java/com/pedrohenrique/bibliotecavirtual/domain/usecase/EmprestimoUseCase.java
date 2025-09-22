package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.EmprestimoValidate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoUseCase {

    private final EmprestimoValidate emprestimoValidate;

    private final EmprestimoOutputPort emprestimoOutputPort;

    public EmprestimoUseCase(EmprestimoValidate emprestimoValidate, EmprestimoOutputPort emprestimoOutputPort) {
        this.emprestimoValidate = emprestimoValidate;
        this.emprestimoOutputPort = emprestimoOutputPort;
    }

    public Emprestimo realizarEmprestimo(Emprestimo emprestimo) {
        try{
            emprestimoValidate.validarEmprestimo(emprestimo);
            emprestimo.setDataEmprestimo(LocalDate.now());
            return emprestimoOutputPort.realizarEmprestimo(emprestimo);
        } catch (BusinessException e){
            throw new BusinessException(e.getMessage());
        }
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(){
        return emprestimoOutputPort.visualizarTodosOsEmprestimos();
    }
}
