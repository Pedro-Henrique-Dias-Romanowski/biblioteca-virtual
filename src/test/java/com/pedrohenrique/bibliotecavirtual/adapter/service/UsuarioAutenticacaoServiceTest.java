package com.pedrohenrique.bibliotecavirtual.adapter.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.AdministradorEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.AdministradorRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticacaoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AdministradorRepository administradorRepository;

    @InjectMocks
    private UsuarioAutenticacaoService usuarioAutenticacaoService;

    private ClienteEntity clienteEntity;
    private AdministradorEntity administradorEntity;
    private String jwtSecret;

    @BeforeEach
    void setUp() {
        jwtSecret = "chavesupersecretaparatestes123456";
        ReflectionTestUtils.setField(usuarioAutenticacaoService, "JWT_SECRET", jwtSecret);

        clienteEntity = new ClienteEntity();
        clienteEntity.setId(1L);
        clienteEntity.setNome("Cliente Teste");
        clienteEntity.setEmail("cliente@teste.com");
        clienteEntity.setSenha("senha123");

        administradorEntity = new AdministradorEntity();
        administradorEntity.setId(1L);
        administradorEntity.setNome("Admin Teste");
        administradorEntity.setEmail("admin@teste.com");
        administradorEntity.setSenha("admin123");
    }

    @Test
    @DisplayName("Deve carregar cliente por email com sucesso")
    void deveCarregarClientePorEmailComSucesso() {
        when(clienteRepository.findByEmailIgnoreCase("cliente@teste.com")).thenReturn(Optional.of(clienteEntity));

        UserDetails userDetails = usuarioAutenticacaoService.loadUserByUsername("cliente@teste.com");

        assertNotNull(userDetails);
        assertEquals("cliente@teste.com", userDetails.getUsername());
        verify(clienteRepository, times(1)).findByEmailIgnoreCase("cliente@teste.com");
    }

    @Test
    @DisplayName("Deve carregar administrador por email com sucesso")
    void deveCarregarAdministradorPorEmailComSucesso() {
        when(clienteRepository.findByEmailIgnoreCase("admin@teste.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByEmailIgnoreCase("admin@teste.com")).thenReturn(Optional.of(administradorEntity));

        UserDetails userDetails = usuarioAutenticacaoService.loadUserByUsername("admin@teste.com");

        assertNotNull(userDetails);
        assertEquals("admin@teste.com", userDetails.getUsername());
        verify(clienteRepository, times(1)).findByEmailIgnoreCase("admin@teste.com");
        verify(administradorRepository, times(1)).findByEmailIgnoreCase("admin@teste.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        when(clienteRepository.findByEmailIgnoreCase("inexistente@teste.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByEmailIgnoreCase("inexistente@teste.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            usuarioAutenticacaoService.loadUserByUsername("inexistente@teste.com");
        });

        verify(clienteRepository, times(1)).findByEmailIgnoreCase("inexistente@teste.com");
        verify(administradorRepository, times(1)).findByEmailIgnoreCase("inexistente@teste.com");
    }

    @Test
    @DisplayName("Deve gerar token para cliente com sucesso")
    void deveGerarTokenParaClienteComSucesso() {
        String token = usuarioAutenticacaoService.gerarTokenCliente(clienteEntity);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar token para administrador com sucesso")
    void deveGerarTokenParaAdministradorComSucesso() {
        String token = usuarioAutenticacaoService.gerarTokenAdministrador(administradorEntity);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção quando houver erro na geração do token para cliente")
    void deveLancarExcecaoQuandoHouverErroNaGeracaoDoTokenParaCliente() {
        try (MockedStatic<JWT> jwtMock = mockStatic(JWT.class)) {
            jwtMock.when(JWT::create).thenThrow(new JWTCreationException("Erro ao gerar token", new Exception()));

            assertThrows(RuntimeException.class, () -> {
                usuarioAutenticacaoService.gerarTokenCliente(clienteEntity);
            });
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando houver erro na geração do token para administrador")
    void deveLancarExcecaoQuandoHouverErroNaGeracaoDoTokenParaAdministrador() {
        try (MockedStatic<JWT> jwtMock = mockStatic(JWT.class)) {
            jwtMock.when(JWT::create).thenThrow(new JWTCreationException("Erro ao gerar token", new Exception()));

            assertThrows(RuntimeException.class, () -> {
                usuarioAutenticacaoService.gerarTokenAdministrador(administradorEntity);
            });
        }
    }

    @Test
    @DisplayName("Deve gerar data de expiração futura para o token")
    void deveGerarDataExpiracaoFuturaParaOToken() {
        Instant dataExpiracao = ReflectionTestUtils.invokeMethod(usuarioAutenticacaoService, "dataExpiracaoToken");
        Instant agora = LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));

        assertNotNull(dataExpiracao);
        assertTrue(dataExpiracao.isAfter(agora));
    }

    @Test
    @DisplayName("Deve gerar data de expiração 30 minutos no futuro")
    void deveGerarDataExpiracao30MinutosNoFuturo() {
        Instant dataExpiracao = ReflectionTestUtils.invokeMethod(usuarioAutenticacaoService, "dataExpiracaoToken");
        Instant agoraMais29Min = LocalDateTime.now().plusMinutes(29).toInstant(ZoneOffset.of("-03:00"));
        Instant agoraMais31Min = LocalDateTime.now().plusMinutes(31).toInstant(ZoneOffset.of("-03:00"));

        assertNotNull(dataExpiracao);
        assertTrue(dataExpiracao.isAfter(agoraMais29Min));
        assertTrue(dataExpiracao.isBefore(agoraMais31Min));
    }
}
