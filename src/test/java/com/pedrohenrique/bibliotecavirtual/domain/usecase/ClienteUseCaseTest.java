package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.ClienteValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteUseCaseTest {

    @Mock
    private ClienteOutputPort clienteOutputPort;

    @Mock
    private ClienteValidate clienteValidate;

    @Mock
    private EmprestimoUseCase emprestimoUseCase;

    @InjectMocks
    private ClienteUseCase clienteUseCase;

    private Cliente cliente;
    private Emprestimo emprestimo;
    private List<Emprestimo> emprestimos;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setSenha("senha123");
        cliente.setQtLivrosEmprestados(1);
        cliente.setPerfil(Perfil.CLIENTE);

        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setClienteId(1L);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now().plusDays(7));
        emprestimo.setAtivo(true);

        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setId(2L);
        emprestimo2.setClienteId(1L);
        emprestimo2.setDataEmprestimo(LocalDate.now().minusDays(14));
        emprestimo2.setDataDevolucao(LocalDate.now().minusDays(7));
        emprestimo2.setAtivo(true);

        emprestimos = Arrays.asList(emprestimo, emprestimo2);
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        doNothing().when(clienteValidate).validarCadastroCliente(cliente);
        when(clienteOutputPort.cadastrarCliente(cliente)).thenReturn(cliente);

        Cliente resultado = clienteUseCase.cadastrarCliente(cliente);

        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(cliente.getNome(), resultado.getNome());
        assertEquals(cliente.getPerfil(), resultado.getPerfil());
        verify(clienteValidate, times(1)).validarCadastroCliente(cliente);
        verify(clienteOutputPort, times(1)).cadastrarCliente(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar cliente com dados inválidos")
    void deveLancarExcecaoAoCadastrarClienteComDadosInvalidos() {
        doThrow(new BusinessException("Email inválido")).when(clienteValidate).validarCadastroCliente(cliente);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteUseCase.cadastrarCliente(cliente);
        });

        assertEquals("Email inválido", exception.getMessage());
        verify(clienteValidate, times(1)).validarCadastroCliente(cliente);
        verify(clienteOutputPort, never()).cadastrarCliente(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve realizar empréstimo com sucesso")
    void deveRealizarEmprestimoComSucesso() {
        when(emprestimoUseCase.realizarEmprestimo(emprestimo)).thenReturn(emprestimo);

        Emprestimo resultado = clienteUseCase.realizarEmprestimo(emprestimo);

        assertNotNull(resultado);
        assertEquals(emprestimo.getId(), resultado.getId());
        assertEquals(emprestimo.getClienteId(), resultado.getClienteId());
        verify(emprestimoUseCase, times(1)).realizarEmprestimo(emprestimo);
    }

    @Test
    @DisplayName("Deve visualizar todos os empréstimos do cliente")
    void deveVisualizarTodosOsEmprestimosDoCliente() {
        when(emprestimoUseCase.visualizarTodosOsEmprestimos(1L)).thenReturn(emprestimos);

        List<Emprestimo> resultado = clienteUseCase.visualizarTodosOsEmprestimos(1L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(emprestimoUseCase, times(1)).visualizarTodosOsEmprestimos(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não tem empréstimos")
    void deveRetornarListaVaziaQuandoClienteNaoTemEmprestimos() {
        when(emprestimoUseCase.visualizarTodosOsEmprestimos(1L)).thenReturn(Collections.emptyList());

        List<Emprestimo> resultado = clienteUseCase.visualizarTodosOsEmprestimos(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(emprestimoUseCase, times(1)).visualizarTodosOsEmprestimos(1L);
    }

    @Test
    @DisplayName("Deve processar solicitação de esqueci minha senha com sucesso")
    void deveProcessarSolicitacaoDeEsqueciMinhaSenhaComSucesso() {
        String email = "joao@email.com";
        doNothing().when(clienteValidate).validarEmailCliente(email);
        doNothing().when(clienteOutputPort).esqueciMinhaSenha(email);

        clienteUseCase.esqueciMinhaSenha(email);

        verify(clienteValidate, times(1)).validarEmailCliente(email);
        verify(clienteOutputPort, times(1)).esqueciMinhaSenha(email);
    }

    @Test
    @DisplayName("Deve lançar exceção ao processar esqueci minha senha com email inválido")
    void deveLancarExcecaoAoProcessarEsqueciMinhaSenhaComEmailInvalido() {
        String email = "emailinvalido";
        doThrow(new BusinessException("Email inválido")).when(clienteValidate).validarEmailCliente(email);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteUseCase.esqueciMinhaSenha(email);
        });

        assertEquals("Email inválido", exception.getMessage());
        verify(clienteValidate, times(1)).validarEmailCliente(email);
        verify(clienteOutputPort, never()).esqueciMinhaSenha(anyString());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void deveAlterarSenhaComSucesso() {
        Integer codigo = 1234;
        String novaSenha = "novaSenha123";
        String confirmacaoSenha = "novaSenha123";
        String email = "joao@email.com";

        doNothing().when(clienteValidate).validarAlteracaoSenha(novaSenha, confirmacaoSenha, email);
        doNothing().when(clienteOutputPort).alterarSenha(codigo, novaSenha, confirmacaoSenha, email);

        clienteUseCase.alterarSenha(codigo, novaSenha, confirmacaoSenha, email);

        verify(clienteValidate, times(1)).validarAlteracaoSenha(novaSenha, confirmacaoSenha, email);
        verify(clienteOutputPort, times(1)).alterarSenha(codigo, novaSenha, confirmacaoSenha, email);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar senha com dados inválidos")
    void deveLancarExcecaoAoAlterarSenhaComDadosInvalidos() {
        Integer codigo = 1234;
        String novaSenha = "novaSenha123";
        String confirmacaoSenha = "outraSenha";
        String email = "joao@email.com";

        doThrow(new BusinessException("Senhas não conferem")).when(clienteValidate)
                .validarAlteracaoSenha(novaSenha, confirmacaoSenha, email);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            clienteUseCase.alterarSenha(codigo, novaSenha, confirmacaoSenha, email);
        });

        assertEquals("Senhas não conferem", exception.getMessage());
        verify(clienteValidate, times(1)).validarAlteracaoSenha(novaSenha, confirmacaoSenha, email);
        verify(clienteOutputPort, never()).alterarSenha(anyInt(), anyString(), anyString(), anyString());
    }
}
