package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import java.util.List;

public interface ClienteOutputPort {

    List<Emprestimo> visualizarTodosOsEmprestimos();
    Emprestimo realizarEmprestimo(Emprestimo emprestimo);
    Cliente cadastrarCliente(Cliente cliente);
    boolean existsByEmail(Cliente cliente);
    boolean existsById(Long id);
}
