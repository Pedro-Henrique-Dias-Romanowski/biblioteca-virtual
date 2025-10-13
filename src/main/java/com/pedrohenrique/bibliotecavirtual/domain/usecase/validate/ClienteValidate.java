package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteExistenteException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.EmailClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.SenhaDiferenteException;
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

    @Value("${mensagem.cliente.senhas.diferentes}")
    private String mensagemClienteSenhasDiferentes;

    public ClienteValidate(ClienteOutputPort clienteOutputPort) {
        this.clienteOutputPort = clienteOutputPort;
    }

    public void validarCliente(Cliente cliente){
        validarNulidadeIdCliente(cliente.getId());
        validarEmailCliente(cliente.getEmail());
        validarExistenciaCliente(cliente);
    }

    public void validarAlteracaoSenha(String senha, String confirmacaoNovaSenha, String email){
        validarEmailCliente(email);
        validarSenha(senha, confirmacaoNovaSenha);
    }

    private void validarNulidadeIdCliente(Long id){
        if (id == null) throw new ClienteInvalidoException(mensagemErroIdClienteNulo + " ID: " + id);
    }

    public void validarEmailCliente(String email){
        if (email == null || email.isBlank()) throw new EmailClienteInvalidoException(mensagemErroEmailInvalido);
    }

    private void validarExistenciaCliente(Cliente cliente){
        if (clienteOutputPort.existsByEmail(cliente)) throw new ClienteExistenteException(mensagemErroEmailExistente);
    }

    private void validarSenha(String senha, String confirmacaoNovaSenha){
        if (!confirmacaoNovaSenha.equals(senha)){
            throw new SenhaDiferenteException(mensagemClienteSenhasDiferentes);
        }
    }
}
