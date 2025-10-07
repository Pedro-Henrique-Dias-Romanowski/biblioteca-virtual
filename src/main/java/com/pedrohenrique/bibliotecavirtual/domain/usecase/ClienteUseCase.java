package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.ClienteValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteUseCase {

    private final ClienteOutputPort clienteOutputPort;

    private final ClienteValidate clienteValidate;

    private final EmprestimoUseCase emprestimoUseCase;

    @Value("${mensagem.cliente.busca.emprestimos}")
    private String mensagemClienteBuscaEmprestimos;

    @Value("${mensagem.cliente.emprestimo}")
    private String mensagemClienteEmprestimo;

    @Value("${mensagem.cliente.cadastrado.sucesso}")
    private String mensagemClienteCadastradoSucesso;

    private final Logger logger = LoggerFactory.getLogger(ClienteUseCase.class);


    public ClienteUseCase(ClienteOutputPort clienteOutputPort, ClienteValidate clienteValidate, EmprestimoUseCase emprestimoUseCase) {
        this.clienteOutputPort = clienteOutputPort;
        this.clienteValidate = clienteValidate;
        this.emprestimoUseCase = emprestimoUseCase;
    }

    public Cliente cadastrarCliente(Cliente cliente) {
        try{
            clienteValidate.validarCliente(cliente);
            cliente.setPerfil(Perfil.CLIENTE);
            logger.info("{}{}", mensagemClienteCadastradoSucesso, cliente.getId());
            return clienteOutputPort.cadastrarCliente(cliente);
        } catch (BusinessException e){
            logger.error("Erro ao cadastrar cliente: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    public Emprestimo realizarEmprestimo(Emprestimo emprestimo) {
        logger.info(mensagemClienteEmprestimo);
        return emprestimoUseCase.realizarEmprestimo(emprestimo);
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(){
        logger.info(mensagemClienteBuscaEmprestimos);
        return emprestimoUseCase.visualizarTodosOsEmprestimos();
    }
}
