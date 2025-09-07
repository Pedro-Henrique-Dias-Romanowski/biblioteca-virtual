package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import org.springframework.stereotype.Service;

@Service
public class ClienteUseCase {

    private final ClienteOutputPort clienteOutputPort;

    public ClienteUseCase(ClienteOutputPort clienteOutputPort) {
        this.clienteOutputPort = clienteOutputPort;
    }

    public Cliente cadastrarCliente(Cliente cliente) throws DataBaseException {
        if (cliente != null && !clienteOutputPort.existsByEmail(cliente)){
            cliente.setPerfil(Perfil.CLIENTE);
            return clienteOutputPort.cadastrarCliente(cliente);
        } else {
            throw new DataBaseException("Cliente nulo ou já cadastrado");
        }
    }
}
