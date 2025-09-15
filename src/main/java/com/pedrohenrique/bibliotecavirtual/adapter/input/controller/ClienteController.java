package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.ClienteControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.ClienteRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LoginRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.ClienteResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LoginResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.service.UsuarioAutenticacaoService;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.ClienteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@RestController
public class ClienteController implements ClienteControllerSwagger {

    private final ClienteMapper clienteMapper;

    private final ClienteUseCase clienteUseCase;

    private final AuthenticationManager authenticationManager;

    private final UsuarioAutenticacaoService usuarioAutenticacaoService;

    public ClienteController(ClienteMapper clienteMapper, ClienteUseCase clienteUseCase, AuthenticationManager authenticationManager, UsuarioAutenticacaoService usuarioAutenticacaoService) {
        this.clienteMapper = clienteMapper;
        this.clienteUseCase = clienteUseCase;
        this.authenticationManager = authenticationManager;
        this.usuarioAutenticacaoService = usuarioAutenticacaoService;
    }

    @Override
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(ClienteRequestDTO clienteRequestDTO) throws DataBaseException {
        var cliente = clienteMapper.toDomain(clienteRequestDTO);
        var clienteSalvo = clienteUseCase.cadastrarCliente(cliente);

        return ResponseEntity.ok().body(clienteMapper.toResponse(clienteSalvo));
    }

    @Override
    public ResponseEntity<LoginResponseDTO> efetuarLogin(LoginRequestDTO loginRequestDTO) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);
        String token = usuarioAutenticacaoService.gerarTokenCliente((ClienteEntity) authentication.getPrincipal());

        return ResponseEntity.ok().body(new LoginResponseDTO(token, LocalDateTime.now()));
    }

    @Override
    @PreAuthorize("hasRole('CLIENTE') and #idCliente == authentication.principal.id")
    public ResponseEntity<List<EmprestimoResponseDTO>> visualizarTodosOsEmprestimos(Long idCliente) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('CLIENTE') and #emprestimoRequestDTO.idUsuario() == authentication.principal.id")
    public ResponseEntity<EmprestimoResponseDTO> realizarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO) {
        return null;
    }
}
