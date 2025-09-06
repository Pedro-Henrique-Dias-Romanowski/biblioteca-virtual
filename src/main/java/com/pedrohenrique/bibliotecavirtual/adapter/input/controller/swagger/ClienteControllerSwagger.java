package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger;


import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.ClienteRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.ClienteResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;


@Tag(name = "Cliente", description = "Operações disponíveis para clientes da biblioteca")
public interface ClienteControllerSwagger {


    @Operation(summary = "Cadastrar cliente", description = "Permite o cadastro de um novo cliente na biblioteca.")
    @PostMapping("clientes")
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@RequestBody ClienteRequestDTO clienteRequestDTO, @RequestHeader String tokenJwt);

    @Operation(summary = "Efetuar login", description = "Permite que o cliente se autentique dentro da biblioteca")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> efetuarLogin(@RequestBody String email, @RequestBody String senha, @RequestHeader String tokenJwt);

    @Operation(summary = "Visualizar empréstimos", description = "Permite que o cliente visualize todos os seus empréstimos feitos na biblioteca")
    @GetMapping("/clientes/livros")
    public ResponseEntity<List<EmprestimoResponseDTO>> visualizarTodosOsEmprestimos(@RequestHeader String tokenJwt);

    @Operation(summary = "Realizar empréstimo", description = "Permite que o cliente realize o empréstimo de um ou mais livros na biblioteca")
    @PostMapping("/clientes/emprestimos")
    public ResponseEntity<EmprestimoResponseDTO> realizarEmprestimo(@RequestBody EmprestimoRequestDTO emprestimoRequestDTO, @RequestHeader String tokenJwt);
}
