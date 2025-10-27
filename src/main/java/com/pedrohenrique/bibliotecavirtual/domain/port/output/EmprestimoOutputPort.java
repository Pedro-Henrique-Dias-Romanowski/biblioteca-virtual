package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;

import java.util.List;
import java.util.Optional;

public interface EmprestimoOutputPort {

    Emprestimo realizarEmprestimo(Emprestimo emprestimo);

    List<Emprestimo> visualizarTodosOsEmprestimos(Long idCliente);

    Emprestimo realizarDevolucaoEmprestimo(Emprestimo emprestimo);

    Boolean existsById(Long idEmprestimo);

    Emprestimo getReferenceById(Long idEmprestimo);

}
