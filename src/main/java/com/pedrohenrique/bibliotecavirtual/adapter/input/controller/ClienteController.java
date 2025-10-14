package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger.ClienteControllerSwagger;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.*;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.*;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.service.UsuarioAutenticacaoService;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.ClienteUseCase;
import org.springframework.beans.factory.annotation.Value;
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

    private final EmprestimoMapper emprestimoMapper;

    private final ClienteUseCase clienteUseCase;

    private final AuthenticationManager authenticationManager;

    private final UsuarioAutenticacaoService usuarioAutenticacaoService;

    @Value("${mensagem.cliente.esqueci.minha.senha}")
    private String mensagemClienteEsqueciMinhaSenha;

    @Value("${mensagem.cliente.senha.alterada.sucesso}")
    private String mensagemClienteSenhaAlteradaSucesso;

    @Value("${mensagem.cliente.redirecionamento.pagina.login}")
    private String mensagemClienteRedirecionamentoPaginaLogin;

    public ClienteController(ClienteMapper clienteMapper, EmprestimoMapper emprestimoMapper, ClienteUseCase clienteUseCase, AuthenticationManager authenticationManager, UsuarioAutenticacaoService usuarioAutenticacaoService) {
        this.clienteMapper = clienteMapper;
        this.emprestimoMapper = emprestimoMapper;
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

        return ResponseEntity.ok().body(new LoginResponseDTO(token, LocalDateTime.now(), ((ClienteEntity) authentication.getPrincipal()).getPerfil(), ((ClienteEntity) authentication.getPrincipal()).getId()));
    }

    @Override
    @PreAuthorize("hasRole('CLIENTE') and #idCliente == authentication.principal.id")
    public ResponseEntity<List<EmprestimoResponseDTO>> visualizarTodosOsEmprestimos(Long idCliente) {
        var listaResponse = clienteUseCase.visualizarTodosOsEmprestimos(idCliente).stream().map(emprestimoMapper::toResponse).toList();
        return ResponseEntity.ok().body(listaResponse);
    }

    @Override
    @PreAuthorize("hasRole('CLIENTE') and #emprestimoRequestDTO.clienteId() == authentication.principal.id")
    public ResponseEntity<EmprestimoResponseDTO> realizarEmprestimo(EmprestimoRequestDTO emprestimoRequestDTO) {
        var emprestimo = emprestimoMapper.toDomain(emprestimoRequestDTO);
        var emprestimoCadastrado = clienteUseCase.realizarEmprestimo(emprestimo);

        return ResponseEntity.ok().body(emprestimoMapper.toResponse(emprestimoCadastrado));
    }

    @Override
    public ResponseEntity<EsqueciMinhaSenhaResponseDTO> esqueciMinhaSenha(EsqueciMinhaSenhaRequestDTO esqueciMinhaSenhaRequestDTO) throws Exception, BusinessException {
        clienteUseCase.esqueciMinhaSenha(esqueciMinhaSenhaRequestDTO.email());
        return ResponseEntity.ok().body(new EsqueciMinhaSenhaResponseDTO(mensagemClienteEsqueciMinhaSenha));
    }

    @Override
    public ResponseEntity<AlterarSenhaResponseDTO> alterarSenha(AlterarSenhaRequestDTO alterarSenhaRequestDTO) throws Exception, BusinessException {
        clienteUseCase.alterarSenha(alterarSenhaRequestDTO.codigo(), alterarSenhaRequestDTO.novaSenha(), alterarSenhaRequestDTO.confirmacaoNovaSenha(), alterarSenhaRequestDTO.email());
        return ResponseEntity.ok().body(new AlterarSenhaResponseDTO(mensagemClienteSenhaAlteradaSucesso, mensagemClienteRedirecionamentoPaginaLogin));
    }
}
