package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import org.springframework.stereotype.Component;

@Component
public class ClienteValidate{

    private final ClienteOutputPort clienteOutputPort;

    public ClienteValidate(ClienteOutputPort clienteOutputPort) {
        this.clienteOutputPort = clienteOutputPort;
    }

    public boolean validarCadastroCliente(Cliente cliente){
        if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isEmpty()) return false;
        return !clienteOutputPort.existsByEmail(cliente);
    }
}
