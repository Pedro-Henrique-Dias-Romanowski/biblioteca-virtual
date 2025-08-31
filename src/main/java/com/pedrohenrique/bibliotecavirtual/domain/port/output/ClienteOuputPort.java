package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;

import java.util.List;
import java.util.Optional;

public interface ClienteOuputPort {

    List<Livro> visualizarTodosOsLivros();
    Optional<Emprestimo> emprestarLivro(Emprestimo emprestimo);
    Cliente cadastrarCliente(Cliente cliente);
    void efetuarLogin();
}
