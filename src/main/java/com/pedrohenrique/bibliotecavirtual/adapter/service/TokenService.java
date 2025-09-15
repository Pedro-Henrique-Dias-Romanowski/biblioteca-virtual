package com.pedrohenrique.bibliotecavirtual.adapter.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${JWT_SECRET}")
    private String JWT_SECRET;


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
}
