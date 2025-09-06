package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.EmprestimoControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import org.springframework.http.ResponseEntity;

public class EmprestimoController implements EmprestimoControllerSwagger {
    @Override
    public ResponseEntity<EmprestimoResponseDTO> cadastrarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO, String tokenJwt) {
        return null;
    }
}
