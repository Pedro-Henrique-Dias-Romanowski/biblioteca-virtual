package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger;


import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LoginRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Adminstrador", description = "Operações disponíveis para administradores da biblioteca")
public interface AdministradorControllerSwagger {

    @Operation(summary = "Efetuar login", description = "Permite que o administrador se autentique dentro da biblioteca")
    @PostMapping("administradores/login")
    public ResponseEntity<LoginResponseDTO> efetuarLogin(@RequestBody LoginRequestDTO loginRequestDTO);
}
