package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;

import java.util.List;

public interface EmprestimoOutputPort {

    Emprestimo realizarEmprestimo(Emprestimo emprestimo);

    List<Emprestimo> visualizarTodosOsEmprestimos();
}
