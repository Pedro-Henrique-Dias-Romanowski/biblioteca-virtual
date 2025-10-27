package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.exceptions.GlobalExceptionHandler;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.*;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.ClienteResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.AdministradorRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.TokenService;
import com.pedrohenrique.bibliotecavirtual.adapter.service.UsuarioAutenticacaoService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.ClienteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteMapper clienteMapper;

    @MockBean
    private EmprestimoMapper emprestimoMapper;

    @MockBean
    private ClienteUseCase clienteUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioAutenticacaoService usuarioAutenticacaoService;

    @MockBean
    ClienteRepository clienteRepository;

    @MockBean
    AdministradorRepository administradorRepository;

    private ClienteEntity clienteEntity;
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
    private DevolucaoEmprestimoRequestDTO devolucaoEmprestimoRequestDTO;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO("cliente@teste.com", "senha123");
        clienteRequestDTO = new ClienteRequestDTO("João Silva", "joao@email.com", "senha123", 2);
        clienteResponseDTO = new ClienteResponseDTO(1L, "João Silva", "joao@email.com", 2);

        clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setNome("João Silva");
        clienteMock.setEmail("joao@email.com");
        clienteMock.setPerfil(Perfil.CLIENTE);

        tokenMock = "token-jwt-valido-123456789";
        emprestimoRequestDTO = new EmprestimoRequestDTO(1L, Arrays.asList(1L, 2L), LocalDate.now().plusDays(7));

        emprestimoMock = new Emprestimo();
        emprestimoMock.setId(1L);
        emprestimoMock.setClienteId(1L);
        emprestimoMock.setLivros(Arrays.asList(1L, 2L));
        emprestimoMock.setAtivo(true);
        emprestimoMock.setDataEmprestimo(LocalDate.now());
        emprestimoMock.setDataDevolucao(LocalDate.now().plusDays(7));

        emprestimoResponseDTO = new EmprestimoResponseDTO(1L, 1L, Arrays.asList(1L, 2L), LocalDate.now(), LocalDate.now().plusDays(7), true);
        esqueciMinhaSenhaRequestDTO = new EsqueciMinhaSenhaRequestDTO("joao@email.com");
        alterarSenhaRequestDTO = new AlterarSenhaRequestDTO("123456", 12345, "novaSenha123", "joao@email.com");
        devolucaoEmprestimoRequestDTO = new DevolucaoEmprestimoRequestDTO(1L, 1L);

        clienteEntity = new ClienteEntity();
        clienteEntity.setId(1L);
        clienteEntity.setPerfil(Perfil.CLIENTE);
    }

    @Test
    @DisplayName("Deve cadastrar um cliente com sucesso")
    void deveCadastrarClienteComSucesso() throws Exception {
        when(clienteMapper.toDomain(any(ClienteRequestDTO.class))).thenReturn(clienteMock);
        when(clienteUseCase.cadastrarCliente(any(Cliente.class))).thenReturn(clienteMock);
        when(clienteMapper.toResponse(any(Cliente.class))).thenReturn(clienteResponseDTO);

        mockMvc.perform(post("/clientes/cadastrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.qtLivrosEmprestados").value(2));

        verify(clienteUseCase, times(1)).cadastrarCliente(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar cliente com email já existente")
    void deveLancarExcecaoAoCadastrarClienteComEmailJaExistente() throws Exception {
        when(clienteMapper.toDomain(any(ClienteRequestDTO.class))).thenReturn(clienteMock);
        when(clienteUseCase.cadastrarCliente(any(Cliente.class)))
                .thenThrow(new BusinessException("Email já cadastrado"));

        mockMvc.perform(post("/clientes/cadastrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email já cadastrado"));

        verify(clienteUseCase, times(1)).cadastrarCliente(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar cliente com nome nulo")
    void deveLancarExcecaoAoCadastrarClienteComNomeNulo() throws Exception {
        when(clienteMapper.toDomain(any(ClienteRequestDTO.class))).thenReturn(clienteMock);
        when(clienteUseCase.cadastrarCliente(any(Cliente.class)))
                .thenThrow(new BusinessException("Nome não pode ser nulo"));

        mockMvc.perform(post("/clientes/cadastrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nome não pode ser nulo"));

        verify(clienteUseCase, times(1)).cadastrarCliente(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve realizar login com credenciais válidas")
    void deveRealizarLoginComCredenciaisValidas() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(clienteEntity, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioAutenticacaoService.gerarTokenCliente(any(ClienteEntity.class))).thenReturn(tokenMock);

        mockMvc.perform(post("/clientes/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenMock))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.perfilAtribuido").value(Perfil.CLIENTE.toString()));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, times(1)).gerarTokenCliente(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar login com credenciais inválidas")
    void deveLancarExcecaoComCredenciaisInvalidas() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/clientes/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, never()).gerarTokenCliente(any(ClienteEntity.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve visualizar todos os empréstimos do cliente")
    void deveVisualizarTodosEmprestimosDoCliente() throws Exception {
        Long idCliente = 1L;
        List<Emprestimo> emprestimos = Collections.singletonList(emprestimoMock);

        when(clienteUseCase.visualizarTodosOsEmprestimos(idCliente)).thenReturn(emprestimos);
        when(emprestimoMapper.toResponse(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        mockMvc.perform(get("/clientes/emprestimos/{idCliente}", idCliente)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(clienteUseCase, times(1)).visualizarTodosOsEmprestimos(idCliente);
        verify(emprestimoMapper, times(1)).toResponse(any(Emprestimo.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve retornar lista vazia quando cliente não possui empréstimos")
    void deveRetornarListaVaziaQuandoClienteNaoPossuiEmprestimos() throws Exception {
        Long idCliente = 1L;

        when(clienteUseCase.visualizarTodosOsEmprestimos(idCliente)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/clientes/emprestimos/{idCliente}", idCliente)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clienteUseCase, times(1)).visualizarTodosOsEmprestimos(idCliente);
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve visualizar múltiplos empréstimos do cliente")
    void deveVisualizarMultiplosEmprestimosDoCliente() throws Exception {
        Long idCliente = 1L;
        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setId(2L);
        emprestimo2.setClienteId(1L);

        List<Emprestimo> emprestimos = Arrays.asList(emprestimoMock, emprestimo2);
        EmprestimoResponseDTO emprestimoResponseDTO2 = new EmprestimoResponseDTO(2L, 1L, List.of(3L), LocalDate.now(), LocalDate.now().plusDays(10), true);

        when(clienteUseCase.visualizarTodosOsEmprestimos(idCliente)).thenReturn(emprestimos);
        when(emprestimoMapper.toResponse(emprestimoMock)).thenReturn(emprestimoResponseDTO);
        when(emprestimoMapper.toResponse(emprestimo2)).thenReturn(emprestimoResponseDTO2);

        mockMvc.perform(get("/clientes/emprestimos/{idCliente}", idCliente)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(clienteUseCase, times(1)).visualizarTodosOsEmprestimos(idCliente);
        verify(emprestimoMapper, times(2)).toResponse(any(Emprestimo.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve realizar empréstimo com sucesso")
    void deveRealizarEmprestimoComSucesso() throws Exception {
        when(emprestimoMapper.toDomain(any(EmprestimoRequestDTO.class))).thenReturn(emprestimoMock);
        when(clienteUseCase.realizarEmprestimo(any(Emprestimo.class))).thenReturn(emprestimoMock);
        when(emprestimoMapper.toResponse(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        mockMvc.perform(post("/clientes/emprestimos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emprestimoRequestDTO)))
                .andExpect(status().isOk());

        verify(clienteUseCase, times(1)).realizarEmprestimo(any(Emprestimo.class));
        verify(emprestimoMapper, times(1)).toResponse(any(Emprestimo.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve lançar exceção ao realizar empréstimo com livro indisponível")
    void deveLancarExcecaoAoRealizarEmprestimoComLivroIndisponivel() throws Exception {
        when(emprestimoMapper.toDomain(any(EmprestimoRequestDTO.class))).thenReturn(emprestimoMock);
        when(clienteUseCase.realizarEmprestimo(any(Emprestimo.class)))
                .thenThrow(new BusinessException("Livro indisponível"));

        mockMvc.perform(post("/clientes/emprestimos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emprestimoRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Livro indisponível"));

        verify(clienteUseCase, times(1)).realizarEmprestimo(any(Emprestimo.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve lançar exceção ao realizar empréstimo excedendo quantidade máxima de livros")
    void deveLancarExcecaoAoRealizarEmprestimoExcedendoQuantidadeMaxima() throws Exception {
        when(emprestimoMapper.toDomain(any(EmprestimoRequestDTO.class))).thenReturn(emprestimoMock);
        when(clienteUseCase.realizarEmprestimo(any(Emprestimo.class)))
                .thenThrow(new BusinessException("Quantidade máxima de livros excedida"));

        mockMvc.perform(post("/clientes/emprestimos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emprestimoRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Quantidade máxima de livros excedida"));

        verify(clienteUseCase, times(1)).realizarEmprestimo(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailRecuperacaoSenhaComSucesso() throws Exception {
        doNothing().when(clienteUseCase).esqueciMinhaSenha(anyString());

        mockMvc.perform(post("/clientes/esqueci-minha-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(esqueciMinhaSenhaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(clienteUseCase, times(1)).esqueciMinhaSenha(esqueciMinhaSenhaRequestDTO.email());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não existe")
    void deveLancarExcecaoQuandoEmailNaoExiste() throws Exception {
        doThrow(new BusinessException("Email não encontrado"))
                .when(clienteUseCase).esqueciMinhaSenha(anyString());

        mockMvc.perform(post("/clientes/esqueci-minha-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(esqueciMinhaSenhaRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email não encontrado"));

        verify(clienteUseCase, times(1)).esqueciMinhaSenha(esqueciMinhaSenhaRequestDTO.email());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void deveAlterarSenhaComSucesso() throws Exception {
        doNothing().when(clienteUseCase).alterarSenha(anyInt(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/clientes/alterar-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarSenhaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists())
                .andExpect(jsonPath("$.redirecionamento").exists());

        verify(clienteUseCase, times(1)).alterarSenha(
                alterarSenhaRequestDTO.codigo(),
                alterarSenhaRequestDTO.novaSenha(),
                alterarSenhaRequestDTO.confirmacaoNovaSenha(),
                alterarSenhaRequestDTO.email()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha e confirmação não correspondem")
    void deveLancarExcecaoQuandoSenhaEConfirmacaoNaoCorrespondem() throws Exception {
        doThrow(new BusinessException("Senha e confirmação não correspondem"))
                .when(clienteUseCase).alterarSenha(anyInt(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/clientes/alterar-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterarSenhaRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Senha e confirmação não correspondem"));

        verify(clienteUseCase, times(1)).alterarSenha(anyInt(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve realizar devolução de empréstimo com sucesso")
    void deveRealizarDevolucaoEmprestimoComSucesso() throws Exception {
        when(emprestimoMapper.devolucaoEmprestimoMapperToDomain(any(DevolucaoEmprestimoRequestDTO.class)))
                .thenReturn(emprestimoMock);
        when(clienteUseCase.realizarDevolucaoEmprestimo(any(Emprestimo.class))).thenReturn(emprestimoMock);
        when(emprestimoMapper.toResponse(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        mockMvc.perform(patch("/clientes/emprestimos/devolucao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devolucaoEmprestimoRequestDTO)))
                .andExpect(status().isOk());

        verify(clienteUseCase, times(1)).realizarDevolucaoEmprestimo(any(Emprestimo.class));
        verify(emprestimoMapper, times(1)).toResponse(any(Emprestimo.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve lançar exceção quando empréstimo não existe para devolução")
    void deveLancarExcecaoQuandoEmprestimoNaoExisteParaDevolucao() throws Exception {
        when(emprestimoMapper.devolucaoEmprestimoMapperToDomain(any(DevolucaoEmprestimoRequestDTO.class)))
                .thenReturn(emprestimoMock);
        when(clienteUseCase.realizarDevolucaoEmprestimo(any(Emprestimo.class)))
                .thenThrow(new BusinessException("Empréstimo não encontrado"));

        mockMvc.perform(patch("/clientes/emprestimos/devolucao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devolucaoEmprestimoRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Empréstimo não encontrado"));

        verify(clienteUseCase, times(1)).realizarDevolucaoEmprestimo(any(Emprestimo.class));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Deve retornar empréstimo inativo após devolução bem-sucedida")
    void deveRetornarEmprestimoInativoAposDevolucaoBemSucedida() throws Exception {
        emprestimoMock.setAtivo(false);
        EmprestimoResponseDTO emprestimoInativo = new EmprestimoResponseDTO(
                1L, 1L, Arrays.asList(1L, 2L),
                LocalDate.now(), LocalDate.now().plusDays(7), false
        );

        when(emprestimoMapper.devolucaoEmprestimoMapperToDomain(any(DevolucaoEmprestimoRequestDTO.class)))
                .thenReturn(emprestimoMock);
        when(clienteUseCase.realizarDevolucaoEmprestimo(any(Emprestimo.class))).thenReturn(emprestimoMock);
        when(emprestimoMapper.toResponse(any(Emprestimo.class))).thenReturn(emprestimoInativo);

        mockMvc.perform(patch("/clientes/emprestimos/devolucao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devolucaoEmprestimoRequestDTO)))
                .andExpect(status().isOk());

        verify(clienteUseCase, times(1)).realizarDevolucaoEmprestimo(any(Emprestimo.class));
        verify(emprestimoMapper, times(1)).toResponse(any(Emprestimo.class));
    }
}

