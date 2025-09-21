package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.EmprestimoInvalidoException;
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
        if (emprestimoValidate.validarEmprestimo(emprestimo)) {
            emprestimo.setDataEmprestimo(LocalDate.now());
            return emprestimoOutputPort.realizarEmprestimo(emprestimo);
        } else {
            throw new EmprestimoInvalidoException("Dados do empréstimo inválido, tente novamente!");
        }
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(){
        return emprestimoOutputPort.visualizarTodosOsEmprestimos();
    }
}
