package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.AdministradorControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LoginRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LoginResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.AdministradorEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.service.UsuarioAutenticacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class AdministradorController implements AdministradorControllerSwagger {

    private final AuthenticationManager authenticationManager;

    private final UsuarioAutenticacaoService usuarioAutenticacaoService;

    public AdministradorController(AuthenticationManager authenticationManager, UsuarioAutenticacaoService usuarioAutenticacaoService) {
        this.authenticationManager = authenticationManager;
        this.usuarioAutenticacaoService = usuarioAutenticacaoService;
    }

    @Override
    public ResponseEntity<LoginResponseDTO> efetuarLogin(LoginRequestDTO loginRequestDTO) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);
        String token = usuarioAutenticacaoService.gerarTokenAdministrador((AdministradorEntity) authentication.getPrincipal());

        return ResponseEntity.ok().body(new LoginResponseDTO(token, LocalDateTime.now()));
    }
}
