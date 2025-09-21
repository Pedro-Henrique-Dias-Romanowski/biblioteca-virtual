package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.EmprestimoInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.ClienteValidate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteUseCase {

    @Value(value = "mensagem.erro.emprestimo.generica")
    private String mensagemErroEmprestimoGenerica;

    private final ClienteOutputPort clienteOutputPort;

    private final ClienteValidate clienteValidate;

    private final EmprestimoUseCase emprestimoUseCase;


    public ClienteUseCase(ClienteOutputPort clienteOutputPort, ClienteValidate clienteValidate, EmprestimoUseCase emprestimoUseCase) {
        this.clienteOutputPort = clienteOutputPort;
        this.clienteValidate = clienteValidate;
        this.emprestimoUseCase = emprestimoUseCase;
    }

    public Cliente cadastrarCliente(Cliente cliente) throws DataBaseException {
        if (clienteValidate.validarCadastroCliente(cliente)) {
            cliente.setPerfil(Perfil.CLIENTE);
            return clienteOutputPort.cadastrarCliente(cliente);
        } else {
            throw new DataBaseException("Cliente nulo ou já cadastrado");
        }
    }

    public Emprestimo realizarEmprestimo(Emprestimo emprestimo) {
        if (emprestimo != null) {
            return emprestimoUseCase.realizarEmprestimo(emprestimo);
        } else {
            throw new EmprestimoInvalidoException(mensagemErroEmprestimoGenerica);
        }
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(){
        return emprestimoUseCase.visualizarTodosOsEmprestimos();
    }
}
