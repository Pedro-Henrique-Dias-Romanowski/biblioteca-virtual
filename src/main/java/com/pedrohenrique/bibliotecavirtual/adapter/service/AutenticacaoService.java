package com.pedrohenrique.bibliotecavirtual.adapter.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    @Value("${APP_JWT_SECRET}")
    private String JWT_SECRET;

    public AutenticacaoService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clienteRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public String gerarToken(ClienteEntity cliente){
        try{
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.create()
                    .withIssuer("Biblioteca Virtual")
                    .withSubject(cliente.getEmail())
                    .withExpiresAt(dataExpiracaoToken())
                    .sign(algorithm);
        } catch(JWTCreationException e){
            // todo lançar um exceção correta, fazer isso depois que criar a exception handler e a exceção personalizada
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String verificarToken(String token){
        DecodedJWT decodedJWT;
        try{
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Biblioteca Virtual")
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch(JWTVerificationException e){
            // todo lancar uma exceção correta, fazer isso depois que criar a exception handler a a exceção personalizada
            throw new RuntimeException("Token JWT inválido ou expirado", e);
        }
    }

    public Instant dataExpiracaoToken(){
        return LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00"));
    }
}
