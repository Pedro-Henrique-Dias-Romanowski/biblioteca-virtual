package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.*;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.*;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.service.UsuarioAutenticacaoService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.ClienteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioAutenticacaoService usuarioAutenticacaoService;

    @Mock
    private Authentication authentication;

    @Mock
    private ClienteEntity clienteEntity;

    @InjectMocks
    private ClienteController clienteController;

    private LoginRequestDTO loginRequestDTO;
    private Cliente clienteMock;
    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;
    private String tokenMock;
    private EmprestimoRequestDTO emprestimoRequestDTO;
    private Emprestimo emprestimoMock;
    private EmprestimoResponseDTO emprestimoResponseDTO;
    private EsqueciMinhaSenhaRequestDTO esqueciMinhaSenhaRequestDTO;
    private AlterarSenhaRequestDTO alterarSenhaRequestDTO;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO("cliente@teste.com", "senha123");
        clienteRequestDTO = new ClienteRequestDTO("João Silva", "joao@email.com", "senha123", 2);
        clienteResponseDTO = new ClienteResponseDTO(1L, "João Silva", "joao@email.com", 2);
        clienteMock = new Cliente();
        tokenMock = "token-jwt-valido-123456789";
        emprestimoRequestDTO = new EmprestimoRequestDTO(1L, Arrays.asList(1L, 2L), LocalDate.now());
        emprestimoMock = new Emprestimo();
        emprestimoResponseDTO = new EmprestimoResponseDTO(1L, 1L, Arrays.asList(1L, 2L), LocalDate.now(), LocalDate.now().plusDays(7), true );
        esqueciMinhaSenhaRequestDTO = new EsqueciMinhaSenhaRequestDTO("joao@email.com");
        alterarSenhaRequestDTO = new AlterarSenhaRequestDTO("123456", 12345, "novaSenha123", "joao@email.com");

        ReflectionTestUtils.setField(clienteController, "mensagemClienteEsqueciMinhaSenha", "Email enviado com sucesso!");
        ReflectionTestUtils.setField(clienteController, "mensagemClienteSenhaAlteradaSucesso", "Senha alterada com sucesso!");
        ReflectionTestUtils.setField(clienteController, "mensagemClienteRedirecionamentoPaginaLogin", "Redirecionando para página de login");
    }

    @Test
    @DisplayName("Deve cadastrar um cliente com sucesso")
    void deveCadastrarClienteComSucesso() throws DataBaseException {
        when(clienteMapper.toDomain(clienteRequestDTO)).thenReturn(clienteMock);
        when(clienteUseCase.cadastrarCliente(clienteMock)).thenReturn(clienteMock);
        when(clienteMapper.toResponse(clienteMock)).thenReturn(clienteResponseDTO);

        ResponseEntity<ClienteResponseDTO> response = clienteController.cadastrarCliente(clienteRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clienteResponseDTO, response.getBody());
        verify(clienteUseCase, times(1)).cadastrarCliente(clienteMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar cliente com dados inválidos")
    void deveLancarExcecaoAoCadastrarClienteComDadosInvalidos() throws BusinessException {
        when(clienteMapper.toDomain(clienteRequestDTO)).thenReturn(clienteMock);
        when(clienteUseCase.cadastrarCliente(clienteMock)).thenThrow(new BusinessException("Erro ao cadastrar cliente"));

        assertThrows(BusinessException.class, () -> {
            clienteController.cadastrarCliente(clienteRequestDTO);
        });

        verify(clienteUseCase, times(1)).cadastrarCliente(clienteMock);
    }

    @Test
    @DisplayName("Deve realizar login com credenciais válidas")
    void deveRealizarLoginComCredenciaisValidas() {
        when(clienteEntity.getId()).thenReturn(1L);
        when(clienteEntity.getPerfil()).thenReturn(Perfil.CLIENTE);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(clienteEntity);
        when(usuarioAutenticacaoService.gerarTokenCliente(clienteEntity)).thenReturn(tokenMock);

        ResponseEntity<LoginResponseDTO> response = clienteController.efetuarLogin(loginRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponseDTO responseDTO = response.getBody();
        assertNotNull(responseDTO);
        assertEquals(tokenMock, responseDTO.token());
        assertNotNull(responseDTO.perfilAtribuido(), "O perfil atribuído não deve ser nulo");
        assertEquals(Perfil.CLIENTE.toString(), responseDTO.perfilAtribuido().toString());
        assertEquals(1L, responseDTO.id());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, times(1)).gerarTokenCliente(clienteEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar login com credenciais inválidas")
    void deveLancarExcecaoComCredenciaisInvalidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(BadCredentialsException.class, () -> {
            clienteController.efetuarLogin(loginRequestDTO);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, never()).gerarTokenCliente(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve visualizar todos os empréstimos do cliente")
    void deveVisualizarTodosEmprestimosDoCliente() {
        Long idCliente = 1L;
        List<Emprestimo> emprestimos = Collections.singletonList(emprestimoMock);
        List<EmprestimoResponseDTO> emprestimosResponse = Collections.singletonList(emprestimoResponseDTO);

        when(clienteUseCase.visualizarTodosOsEmprestimos(idCliente)).thenReturn(emprestimos);
        when(emprestimoMapper.toResponse(emprestimoMock)).thenReturn(emprestimoResponseDTO);

        ResponseEntity<List<EmprestimoResponseDTO>> response = clienteController.visualizarTodosOsEmprestimos(idCliente);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(emprestimosResponse.size(), response.getBody().size());
        verify(clienteUseCase, times(1)).visualizarTodosOsEmprestimos(idCliente);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não possui empréstimos")
    void deveRetornarListaVaziaQuandoClienteNaoPossuiEmprestimos() {
        Long idCliente = 1L;
        List<Emprestimo> emprestimos = List.of();

        when(clienteUseCase.visualizarTodosOsEmprestimos(idCliente)).thenReturn(emprestimos);

        ResponseEntity<List<EmprestimoResponseDTO>> response = clienteController.visualizarTodosOsEmprestimos(idCliente);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(clienteUseCase, times(1)).visualizarTodosOsEmprestimos(idCliente);
    }

    @Test
    @DisplayName("Deve realizar empréstimo com sucesso")
    void deveRealizarEmprestimoComSucesso() {
        when(emprestimoMapper.toDomain(emprestimoRequestDTO)).thenReturn(emprestimoMock);
        when(clienteUseCase.realizarEmprestimo(emprestimoMock)).thenReturn(emprestimoMock);
        when(emprestimoMapper.toResponse(emprestimoMock)).thenReturn(emprestimoResponseDTO);

        ResponseEntity<EmprestimoResponseDTO> response = clienteController.realizarEmprestimo(emprestimoRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emprestimoResponseDTO, response.getBody());
        verify(clienteUseCase, times(1)).realizarEmprestimo(emprestimoMock);
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailRecuperacaoSenhaComSucesso() throws Exception, BusinessException {
        doNothing().when(clienteUseCase).esqueciMinhaSenha(anyString());

        ResponseEntity<EsqueciMinhaSenhaResponseDTO> response = clienteController.esqueciMinhaSenha(esqueciMinhaSenhaRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email enviado com sucesso!", response.getBody().mensagem());
        verify(clienteUseCase, times(1)).esqueciMinhaSenha(esqueciMinhaSenhaRequestDTO.email());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não existe")
    void deveLancarExcecaoQuandoEmailNaoExiste() throws Exception, BusinessException {
        doThrow(new BusinessException("Email não encontrado")).when(clienteUseCase).esqueciMinhaSenha(anyString());

        assertThrows(BusinessException.class, () -> {
            clienteController.esqueciMinhaSenha(esqueciMinhaSenhaRequestDTO);
        });

        verify(clienteUseCase, times(1)).esqueciMinhaSenha(esqueciMinhaSenhaRequestDTO.email());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void deveAlterarSenhaComSucesso() throws Exception, BusinessException {
        doNothing().when(clienteUseCase).alterarSenha(anyInt(), anyString(), anyString(), anyString());

        ResponseEntity<AlterarSenhaResponseDTO> response = clienteController.alterarSenha(alterarSenhaRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Senha alterada com sucesso!", response.getBody().mensagem());
        assertEquals("Redirecionando para página de login", response.getBody().redirecionamento());
        verify(clienteUseCase, times(1)).alterarSenha(
                alterarSenhaRequestDTO.codigo(),
                alterarSenhaRequestDTO.novaSenha(),
                alterarSenhaRequestDTO.confirmacaoNovaSenha(),
                alterarSenhaRequestDTO.email()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha e confirmação não correspondem")
    void deveLancarExcecaoQuandoSenhaEConfirmacaoNaoCorrespondem() throws Exception, BusinessException {
        doThrow(new BusinessException("Senha e confirmação não correspondem")).when(clienteUseCase).alterarSenha(
                anyInt(), anyString(), anyString(), anyString());

        assertThrows(BusinessException.class, () -> {
            clienteController.alterarSenha(alterarSenhaRequestDTO);
        });

        verify(clienteUseCase, times(1)).alterarSenha(
                alterarSenhaRequestDTO.codigo(),
                alterarSenhaRequestDTO.novaSenha(),
                alterarSenhaRequestDTO.confirmacaoNovaSenha(),
                alterarSenhaRequestDTO.email()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando código de recuperação é inválido")
    void deveLancarExcecaoQuandoCodigoRecuperacaoInvalido() throws Exception, BusinessException {
        doThrow(new BusinessException("Código de recuperação inválido")).when(clienteUseCase).alterarSenha(
                anyInt(), anyString(), anyString(), anyString());

        assertThrows(BusinessException.class, () -> {
            clienteController.alterarSenha(alterarSenhaRequestDTO);
        });

        verify(clienteUseCase, times(1)).alterarSenha(
                alterarSenhaRequestDTO.codigo(),
                alterarSenhaRequestDTO.novaSenha(),
                alterarSenhaRequestDTO.confirmacaoNovaSenha(),
                alterarSenhaRequestDTO.email()
        );
    }
}
