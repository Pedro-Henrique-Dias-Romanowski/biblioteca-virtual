package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.ClienteControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.ClienteRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.ClienteResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LoginResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ClienteController implements ClienteControllerSwagger {
    @Override
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(ClienteRequestDTO clienteRequestDTO, String tokenJwt) {
        return null;
    }

    @Override
    public ResponseEntity<LoginResponseDTO> efetuarLogin(String email, String senha, String tokenJwt) {
        return null;
    }

    @Override
    public ResponseEntity<List<EmprestimoResponseDTO>> visualizarTodosOsEmprestimos(String tokenJwt) {
        return null;
    }

    @Override
    public ResponseEntity<EmprestimoResponseDTO> realizarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO, String tokenJwt) {
        return null;
    }
}
