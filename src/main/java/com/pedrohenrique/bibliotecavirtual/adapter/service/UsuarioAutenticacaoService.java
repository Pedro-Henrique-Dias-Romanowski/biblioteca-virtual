package com.pedrohenrique.bibliotecavirtual.adapter.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.AdministradorEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.AdministradorRepository;
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
public class UsuarioAutenticacaoService implements UserDetailsService {

    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;

    @Value("${JWT_SECRET}")
    private String JWT_SECRET;

    public UsuarioAutenticacaoService(ClienteRepository clienteRepository, AdministradorRepository administradorRepository) {
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clienteRepository.findByEmailIgnoreCase(username)
                .map(user -> (UserDetails) user)
                .or(() -> administradorRepository.findByEmailIgnoreCase(username)
                        .map(user -> (UserDetails) user))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public String gerarTokenCliente(ClienteEntity cliente){
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

    public String gerarTokenAdministrador(AdministradorEntity administrador){
        try{
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.create()
                    .withIssuer("Biblioteca Virtual")
                    .withSubject(administrador.getEmail())
                    .withExpiresAt(dataExpiracaoToken())
                    .sign(algorithm);
        } catch(JWTCreationException e){
            // todo lançar um exceção correta, fazer isso depois que criar a exception handler e a exceção personalizada
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public Instant dataExpiracaoToken(){
        return LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00"));
    }
}
