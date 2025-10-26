package com.pedrohenrique.bibliotecavirtual.adapter.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private String tokenValido;
    private String tokenInvalido;
    private String jwtSecret;
    private String emailUsuario;

    @BeforeEach
    void setUp() {
        jwtSecret = "chavesupersecretaparatestes123456";
        emailUsuario = "usuario@teste.com";
        ReflectionTestUtils.setField(tokenService, "JWT_SECRET", jwtSecret);

        // Criação de um token válido para testes
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        tokenValido = JWT.create()
                .withIssuer("Biblioteca Virtual")
                .withSubject(emailUsuario)
                .sign(algorithm);

        // Token inválido para testes
        tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3VhcmlvQHRlc3RlLmNvbSIsImlzcyI6IkJpYmxpb3RlY2EgVmlydHVhbCIsImlhdCI6MTUxNjIzOTAyMn0.invalida";
    }

    @Test
    @DisplayName("Deve verificar token válido com sucesso")
    void deveVerificarTokenValidoComSucesso() {
        String resultado = tokenService.verificarToken(tokenValido);

        assertEquals(emailUsuario, resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção quando token é inválido")
    void deveLancarExcecaoQuandoTokenInvalido() {
        assertThrows(RuntimeException.class, () -> {
            tokenService.verificarToken(tokenInvalido);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando token está vazio")
    void deveLancarExcecaoQuandoTokenVazio() {
        assertThrows(RuntimeException.class, () -> {
            tokenService.verificarToken("");
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando token é nulo")
    void deveLancarExcecaoQuandoTokenNulo() {
        assertThrows(RuntimeException.class, () -> {
            tokenService.verificarToken(null);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando emissor é diferente do esperado")
    void deveLancarExcecaoQuandoEmissorDiferente() {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        String tokenEmissorErrado = JWT.create()
                .withIssuer("Emissor Errado")
                .withSubject(emailUsuario)
                .sign(algorithm);

        assertThrows(RuntimeException.class, () -> {
            tokenService.verificarToken(tokenEmissorErrado);
        });
    }

    @Test
    @DisplayName("Deve verificar mensagem de erro quando token é inválido")
    void deveVerificarMensagemErroQuandoTokenInvalido() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tokenService.verificarToken(tokenInvalido);
        });

        assertTrue(exception.getMessage().contains("Token JWT invalido ou expirado"));
    }
}
