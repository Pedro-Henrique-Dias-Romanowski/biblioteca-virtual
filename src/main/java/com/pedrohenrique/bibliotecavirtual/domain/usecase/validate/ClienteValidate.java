package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteExistenteException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.EmailClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClienteValidate{

    private final ClienteOutputPort clienteOutputPort;

    @Value("${mensagem.erro.cliente.email.invalido}")
    private String mensagemErroEmailInvalido;

    @Value("${mensagem.erro.cliente.email.existente}")
    private String mensagemErroEmailExistente;

    @Value("${mensagem.erro.dados.cliente.id.nulo}")
    private String mensagemErroIdClienteNulo;

    public ClienteValidate(ClienteOutputPort clienteOutputPort) {
        this.clienteOutputPort = clienteOutputPort;
    }

    public void validarCliente(Cliente cliente){
        validarNulidadeIdCliente(cliente.getId());
        validarEmailCliente(cliente.getEmail());
        validarExistenciaCliente(cliente);
    }

    private void validarNulidadeIdCliente(Long id){
        if (id == null) throw new ClienteInvalidoException(mensagemErroIdClienteNulo + " ID: " + id);
    }

    private void validarEmailCliente(String email){
        if (email == null || email.isBlank()) throw new EmailClienteInvalidoException(mensagemErroEmailInvalido);
    }

    private void validarExistenciaCliente(Cliente cliente){
        if (clienteOutputPort.existsByEmail(cliente)) throw new ClienteExistenteException(mensagemErroEmailExistente);
    }
}
