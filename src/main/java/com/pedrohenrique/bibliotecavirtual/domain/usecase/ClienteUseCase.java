package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.ClienteValidate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteUseCase {

    private final ClienteOutputPort clienteOutputPort;

    private final ClienteValidate clienteValidate;

    private final EmprestimoUseCase emprestimoUseCase;


    public ClienteUseCase(ClienteOutputPort clienteOutputPort, ClienteValidate clienteValidate, EmprestimoUseCase emprestimoUseCase) {
        this.clienteOutputPort = clienteOutputPort;
        this.clienteValidate = clienteValidate;
        this.emprestimoUseCase = emprestimoUseCase;
    }

    public Cliente cadastrarCliente(Cliente cliente) throws DataBaseException {
        try{
            clienteValidate.validarCliente(cliente);
            cliente.setPerfil(Perfil.CLIENTE);
            return clienteOutputPort.cadastrarCliente(cliente);
        } catch (BusinessException e){
            throw new BusinessException(e.getMessage());
        }
    }

    public Emprestimo realizarEmprestimo(Emprestimo emprestimo) {
        return emprestimoUseCase.realizarEmprestimo(emprestimo);
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(){
        return emprestimoUseCase.visualizarTodosOsEmprestimos();
    }
}
