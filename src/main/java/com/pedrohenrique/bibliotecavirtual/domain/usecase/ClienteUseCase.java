package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.ClienteValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteUseCase {

    private final ClienteOutputPort clienteOutputPort;

    private final ClienteValidate clienteValidate;

    private final EmprestimoUseCase emprestimoUseCase;

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
            logger.info("Cliente cadastrado com sucesso {}", cliente.getId());
            return clienteOutputPort.cadastrarCliente(cliente);
        } catch (BusinessException e){
            logger.error("Erro ao cadastrar cliente: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    public Emprestimo realizarEmprestimo(Emprestimo emprestimo) {
        logger.info("Cliente do id {} realizando emprestimo", emprestimo.getClienteId());
        return emprestimoUseCase.realizarEmprestimo(emprestimo);
    }

    public List<Emprestimo> visualizarTodosOsEmprestimos(){
        logger.info("cliente realizando busca de seus emprestimos");
        return emprestimoUseCase.visualizarTodosOsEmprestimos();
    }

    public void esqueciMinhaSenha(String email){
        clienteValidate.validarEmailCliente(email);
        clienteOutputPort.esqueciMinhaSenha(email);
        logger.info("Solicitação de recuperação de senha enviada para o email {}", email);
    }

    public void alterarSenha(Integer codigo, String senha, String confirmacaoNovaSenha, String email){
        try{
            clienteValidate.validarAlteracaoSenha(senha, confirmacaoNovaSenha, email);
            clienteOutputPort.alterarSenha(codigo, senha, confirmacaoNovaSenha, email);
            logger.info("Senha do cliente alterada com sucesso");
        } catch (BusinessException e){
            logger.error("Erro ao alterar senha do cliente: {}", e.getMessage());
            throw new BusinessException(e.getMessage());

        }
    };
}
