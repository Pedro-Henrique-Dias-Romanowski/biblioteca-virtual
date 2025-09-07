package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.EmprestimoControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

public class EmprestimoController implements EmprestimoControllerSwagger{

    @Override
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<EmprestimoResponseDTO> cadastrarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO, String tokenJwt) {
        return null;
    }
}
