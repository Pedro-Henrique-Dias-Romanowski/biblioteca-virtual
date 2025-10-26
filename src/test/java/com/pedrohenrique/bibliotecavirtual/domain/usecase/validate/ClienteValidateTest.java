package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteExistenteException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.EmailClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.SenhaDiferenteException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteValidateTest {

    @Mock
    private ClienteOutputPort clienteOutputPort;

    @InjectMocks
    private ClienteValidate clienteValidate;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEmail("teste@email.com");

        ReflectionTestUtils.setField(clienteValidate, "mensagemErroEmailInvalido", "O email é invalido, tente novamente");
        ReflectionTestUtils.setField(clienteValidate, "mensagemErroEmailExistente", "Esse email ja foi cadastrado, tente novamente");
        ReflectionTestUtils.setField(clienteValidate, "mensagemErroIdClienteNulo", "O ID do cliente nao pode ser nulo");
        ReflectionTestUtils.setField(clienteValidate, "mensagemClienteSenhasDiferentes", "As duas senhas devem ser iguais tente novamente!");
    }

    @Test
    @DisplayName("Deve validar cliente com sucesso quando todos os dados estão corretos")
    void deveValidarClienteComSucessoQuandoTodosDadosEstaoCorretos() {
        when(clienteOutputPort.existsByEmail(cliente)).thenReturn(false);

        assertDoesNotThrow(() -> clienteValidate.validarCliente(cliente));

        verify(clienteOutputPort).existsByEmail(cliente);
    }


    @Test
    @DisplayName("Deve lançar exceção quando ID do cliente é nulo")
    void deveLancarExcecaoQuandoIdDoClienteEhNulo() {
        cliente.setId(null);

        ClienteInvalidoException exception = assertThrows(
            ClienteInvalidoException.class,
            () -> clienteValidate.validarCliente(cliente)
        );

        assertTrue(exception.getMessage().contains("O ID do cliente nao pode ser nulo"));
        assertTrue(exception.getMessage().contains("ID: null"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é nulo")
    void deveLancarExcecaoQuandoEmailEhNulo() {
        cliente.setEmail(null);

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarCliente(cliente)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email está em branco")
    void deveLancarExcecaoQuandoEmailEstaEmBranco() {
        cliente.setEmail("   ");

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarCliente(cliente)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é string vazia")
    void deveLancarExcecaoQuandoEmailEhStringVazia() {
        cliente.setEmail("");

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarCliente(cliente)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente já existe")
    void deveLancarExcecaoQuandoClienteJaExiste() {
        when(clienteOutputPort.existsByEmail(cliente)).thenReturn(true);

        ClienteExistenteException exception = assertThrows(
            ClienteExistenteException.class,
            () -> clienteValidate.validarCliente(cliente)
        );

        assertEquals("Esse email ja foi cadastrado, tente novamente", exception.getMessage());
        verify(clienteOutputPort).existsByEmail(cliente);
    }


    @Test
    @DisplayName("Deve lançar exceção no cadastro quando email é inválido")
    void deveLancarExcecaoNoCadastroQuandoEmailEhInvalido() {
        cliente.setEmail(null);

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarCadastroCliente(cliente)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
        verify(clienteOutputPort, never()).existsByEmail(any());
    }

    @Test
    @DisplayName("Deve lançar exceção no cadastro quando cliente já existe")
    void deveLancarExcecaoNoCadastroQuandoClienteJaExiste() {
        when(clienteOutputPort.existsByEmail(cliente)).thenReturn(true);

        ClienteExistenteException exception = assertThrows(
            ClienteExistenteException.class,
            () -> clienteValidate.validarCadastroCliente(cliente)
        );

        assertEquals("Esse email ja foi cadastrado, tente novamente", exception.getMessage());
        verify(clienteOutputPort).existsByEmail(cliente);
    }

    @Test
    @DisplayName("Deve validar alteração de senha com sucesso quando dados estão corretos")
    void deveValidarAlteracaoSenhaComSucessoQuandoDadosEstaoCorretos() {
        String senha = "novaSenha123";
        String confirmacaoSenha = "novaSenha123";
        String email = "teste@email.com";

        assertDoesNotThrow(() ->
            clienteValidate.validarAlteracaoSenha(senha, confirmacaoSenha, email)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção na alteração de senha quando email é inválido")
    void deveLancarExcecaoNaAlteracaoSenhaQuandoEmailEhInvalido() {
        String senha = "novaSenha123";
        String confirmacaoSenha = "novaSenha123";
        String email = null;

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarAlteracaoSenha(senha, confirmacaoSenha, email)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção na alteração de senha quando senhas são diferentes")
    void deveLancarExcecaoNaAlteracaoSenhaQuandoSenhasSaoDiferentes() {
        String senha = "novaSenha123";
        String confirmacaoSenha = "senhasDiferentes456";
        String email = "teste@email.com";

        SenhaDiferenteException exception = assertThrows(
            SenhaDiferenteException.class,
            () -> clienteValidate.validarAlteracaoSenha(senha, confirmacaoSenha, email)
        );

        assertEquals("As duas senhas devem ser iguais tente novamente!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção na alteração quando email está em branco")
    void deveLancarExcecaoNaAlteracaoQuandoEmailEstaEmBranco() {
        String senha = "novaSenha123";
        String confirmacaoSenha = "novaSenha123";
        String email = "  ";

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarAlteracaoSenha(senha, confirmacaoSenha, email)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar email público com sucesso quando email é válido")
    void deveValidarEmailPublicoComSucessoQuandoEmailEhValido() {
        String email = "teste@email.com";

        assertDoesNotThrow(() -> clienteValidate.validarEmailCliente(email));
    }

    @Test
    @DisplayName("Deve lançar exceção na validação pública quando email é nulo")
    void deveLancarExcecaoNaValidacaoPublicaQuandoEmailEhNulo() {
        String email = null;

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarEmailCliente(email)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção na validação pública quando email está vazio")
    void deveLancarExcecaoNaValidacaoPublicaQuandoEmailEstaVazio() {
        String email = "";

        EmailClienteInvalidoException exception = assertThrows(
            EmailClienteInvalidoException.class,
            () -> clienteValidate.validarEmailCliente(email)
        );

        assertEquals("O email é invalido, tente novamente", exception.getMessage());
    }

}
