package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger;


import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.ClienteRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LoginRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.ClienteResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LoginResponseDTO;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Tag(name = "Cliente", description = "Operações disponíveis para clientes da biblioteca")
public interface ClienteControllerSwagger {


    @Operation(summary = "Cadastrar cliente", description = "Permite o cadastro de um novo cliente na biblioteca.")
    @PostMapping("clientes/cadastrar")
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@RequestBody ClienteRequestDTO clienteRequestDTO) throws Exception, BusinessException;

    @Operation(summary = "Efetuar login", description = "Permite que o cliente se autentique dentro da biblioteca")
    @PostMapping("clientes/login")
    public ResponseEntity<LoginResponseDTO> efetuarLogin(@RequestBody LoginRequestDTO loginRequestDTO) throws Exception, BusinessException;

    @Operation(summary = "Visualizar empréstimos", description = "Permite que o cliente visualize todos os seus empréstimos feitos na biblioteca")
    @GetMapping("/clientes/emprestimos/{idCliente}")
    public ResponseEntity<List<EmprestimoResponseDTO>> visualizarTodosOsEmprestimos(@PathVariable Long idCliente) throws Exception, BusinessException;

    @Operation(summary = "Realizar empréstimo", description = "Permite que o cliente realize o empréstimo de um ou mais livros na biblioteca")
    @PostMapping("/clientes/emprestimos")
    public ResponseEntity<EmprestimoResponseDTO> realizarEmprestimo(@RequestBody EmprestimoRequestDTO emprestimoRequestDTO) throws Exception, BusinessException;
}
