package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;

public interface EmprestimoOutputPort {

    Emprestimo cadastrarEmprestimo(Emprestimo emprestimo);
}
