package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LoginRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LoginResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.AdministradorEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.service.UsuarioAutenticacaoService;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministradorControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioAutenticacaoService usuarioAutenticacaoService;

    @Mock
    private AdministradorEntity administradorEntity;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdministradorController administradorController;

    private LoginRequestDTO loginRequestDTO;

    private String tokenMock;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO("admin@teste.com", "senha123");
        tokenMock = "token-jwt-valido-123456789";
    }

    @Test
    @DisplayName("Deve realizar login com credenciais válidas")
    void deveRealizarLoginComCredenciaisValidas() {
        // Configuração dos mocks
        when(administradorEntity.getId()).thenReturn(1L);
        when(administradorEntity.getPerfil()).thenReturn(Perfil.ADMIN);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(administradorEntity);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(administradorEntity)).thenReturn(tokenMock);

        ResponseEntity<LoginResponseDTO> response = administradorController.efetuarLogin(loginRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponseDTO responseDTO = response.getBody();
        assertNotNull(responseDTO);
        assertEquals(tokenMock, responseDTO.token());
        assertNotNull(responseDTO.perfilAtribuido(), "O perfil atribuído não deve ser nulo");
        assertEquals(Perfil.ADMIN.toString(), responseDTO.perfilAtribuido().toString());
        assertEquals(1L, responseDTO.id());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, times(1)).gerarTokenAdministrador(administradorEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar login com credenciais inválidas")
    void deveLancarExcecaoComCredenciaisInvalidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(BadCredentialsException.class, () -> {
            administradorController.efetuarLogin(loginRequestDTO);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioAutenticacaoService, never()).gerarTokenAdministrador(any(AdministradorEntity.class));
    }

    @Test
    @DisplayName("Deve verificar se o token está sendo gerado corretamente")
    void deveVerificarGeracaoCorretaDoToken() {
        // Configuração dos mocks
        when(administradorEntity.getId()).thenReturn(1L);
        when(administradorEntity.getPerfil()).thenReturn(Perfil.ADMIN);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(administradorEntity);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(administradorEntity)).thenReturn(tokenMock);

        ResponseEntity<LoginResponseDTO> response = administradorController.efetuarLogin(loginRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(tokenMock, response.getBody().token());
        verify(usuarioAutenticacaoService, times(1)).gerarTokenAdministrador(administradorEntity);
    }

    @Test
    @DisplayName("Deve retornar dados do usuário junto com o token")
    void deveRetornarDadosDoUsuarioJuntoComToken() {
        // Configuração dos mocks
        when(administradorEntity.getId()).thenReturn(1L);
        when(administradorEntity.getPerfil()).thenReturn(Perfil.ADMIN);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(administradorEntity);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(administradorEntity)).thenReturn(tokenMock);

        ResponseEntity<LoginResponseDTO> response = administradorController.efetuarLogin(loginRequestDTO);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertNotNull(response.getBody().perfilAtribuido(), "O perfil atribuído não deve ser nulo");
        assertEquals(Perfil.ADMIN.toString(), response.getBody().perfilAtribuido().toString());
        assertNotNull(response.getBody().dataHoraLogin());
    }

    @Test
    @DisplayName("Deve garantir que a data do login seja a data atual")
    void deveGarantirQueDataLoginSejaAtual() {
        // Configuração dos mocks
        when(administradorEntity.getId()).thenReturn(1L);
        when(administradorEntity.getPerfil()).thenReturn(Perfil.ADMIN);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(administradorEntity);
        when(usuarioAutenticacaoService.gerarTokenAdministrador(administradorEntity)).thenReturn(tokenMock);

        ResponseEntity<LoginResponseDTO> response = administradorController.efetuarLogin(loginRequestDTO);

        assertNotNull(response.getBody());
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataHoraRetornada = response.getBody().dataHoraLogin();

        assertNotNull(dataHoraRetornada);
        assertTrue(dataHoraRetornada.getYear() == agora.getYear() &&
                   dataHoraRetornada.getMonth() == agora.getMonth() &&
                   dataHoraRetornada.getDayOfMonth() == agora.getDayOfMonth() &&
                   dataHoraRetornada.getHour() == agora.getHour());
    }
}
