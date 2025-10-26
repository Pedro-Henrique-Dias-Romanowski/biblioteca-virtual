package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.exceptions.GlobalExceptionHandler;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LoginRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.AdministradorEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.AdministradorRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.TokenService;
import com.pedrohenrique.bibliotecavirtual.adapter.service.UsuarioAutenticacaoService;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdministradorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioAutenticacaoService usuarioAutenticacaoService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    ClienteRepository clienteRepository;

    @MockBean
    AdministradorRepository administradorRepository;

    private AdministradorEntity administradorEntity;
    private LoginRequestDTO loginRequestDTO;
    private String tokenMock;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO("admin@teste.com", "senha123");
        tokenMock = "token-jwt-admin-valido-123456789";

        administradorEntity = new AdministradorEntity();
        administradorEntity.setId(1L);
        administradorEntity.setPerfil(Perfil.ADMIN);
    }

    @Test
    @DisplayName("Deve realizar login de administrador com credenciais válidas")
    void deveRealizarLoginComCredenciaisValidas() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(administradorEntity, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(any(AdministradorEntity.class)))
                .thenReturn(tokenMock);

        mockMvc.perform(post("/administradores/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenMock))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.perfilAtribuido").value(Perfil.ADMIN.toString()))
                .andExpect(jsonPath("$.dataHoraLogin").exists());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, times(1)).gerarTokenAdministrador(any(AdministradorEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar login com credenciais inválidas")
    void deveLancarExcecaoComCredenciaisInvalidas() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/administradores/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, never()).gerarTokenAdministrador(any(AdministradorEntity.class));
    }

    @Test
    @DisplayName("Deve verificar se o token está sendo gerado corretamente")
    void deveVerificarGeracaoCorretaDoToken() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(administradorEntity, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(any(AdministradorEntity.class)))
                .thenReturn(tokenMock);

        mockMvc.perform(post("/administradores/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenMock));

        verify(usuarioAutenticacaoService, times(1)).gerarTokenAdministrador(any(AdministradorEntity.class));
    }

    @Test
    @DisplayName("Deve retornar dados do administrador junto com o token")
    void deveRetornarDadosDoAdministradorJuntoComToken() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(administradorEntity, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(any(AdministradorEntity.class)))
                .thenReturn(tokenMock);

        mockMvc.perform(post("/administradores/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.perfilAtribuido").value(Perfil.ADMIN.toString()))
                .andExpect(jsonPath("$.dataHoraLogin").exists());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Deve garantir que a data do login seja retornada")
    void deveGarantirQueDataLoginSejaRetornada() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(administradorEntity, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(any(AdministradorEntity.class)))
                .thenReturn(tokenMock);

        mockMvc.perform(post("/administradores/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataHoraLogin").isNotEmpty());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Deve retornar perfil ADMIN correto no response")
    void deveRetornarPerfilAdminCorretoNoResponse() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(administradorEntity, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(any(AdministradorEntity.class)))
                .thenReturn(tokenMock);

        mockMvc.perform(post("/administradores/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfilAtribuido").value("ADMIN"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Deve chamar authenticationManager com email e senha corretos")
    void deveChamarAuthenticationManagerComCredenciaisCorretas() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(administradorEntity, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(any(AdministradorEntity.class)))
                .thenReturn(tokenMock);

        mockMvc.perform(post("/administradores/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk());

        verify(authenticationManager).authenticate(argThat(auth1 ->
                auth1 instanceof UsernamePasswordAuthenticationToken &&
                auth1.getPrincipal().equals("admin@teste.com") &&
                auth1.getCredentials().equals("senha123")
        ));
    }
}

