package com.pedrohenrique.bibliotecavirtual.domain.port.output;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import java.util.List;

public interface ClienteOutputPort {

    Cliente cadastrarCliente(Cliente cliente);
    boolean existsByEmail(Cliente cliente);
    boolean existsById(Long id);
    void esqueciMinhaSenha(String email);
    void alterarSenha(Integer codigo, String novaSenha, String confirmacaoNovaSenha, String email);
}
