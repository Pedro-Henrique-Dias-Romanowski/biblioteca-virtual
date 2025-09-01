package com.pedrohenrique.bibliotecavirtual.adapter.input.security.config;

import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.AutenticacaoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final ClienteRepository clienteRepository;
    private final AutenticacaoService autenticacaoService;

    public SecurityFilter(ClienteRepository clienteRepository, AutenticacaoService autenticacaoService) {
        this.clienteRepository = clienteRepository;
        this.autenticacaoService = autenticacaoService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarTokenRequisicao(request);

        if (token != null){
            String email = autenticacaoService.verificarToken(token);
            var cliente = clienteRepository.findByEmailIgnoreCase(email)
                    .orElseThrow();
            Authentication authentication = new UsernamePasswordAuthenticationToken(cliente, null, cliente.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarTokenRequisicao(HttpServletRequest request){
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null){
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
