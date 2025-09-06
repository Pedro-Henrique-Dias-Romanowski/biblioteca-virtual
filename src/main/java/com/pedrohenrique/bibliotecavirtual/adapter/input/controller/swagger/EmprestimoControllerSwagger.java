package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger;


import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.EmprestimoRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.EmprestimoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Empréstimo", description = "Operações relacionadas a empréstimos de livros")
public interface EmprestimoControllerSwagger {

    @Operation(summary = "Cadastrar um empréstimo", description = "Permite cadastrar um empréstimo após a realização de um empréstimo feita por um cliente")
    @PostMapping("/emprestimos")
    // ESSA URL SÓ VAI SER ACESSADA PELO CLIENTE, POIS É ELE QUE VAI FAZER O EMPRÉSTIMO
    public ResponseEntity<EmprestimoResponseDTO> cadastrarEmprestimo(@RequestBody EmprestimoRequestDTO emprestimoRequestDTO, @RequestHeader String tokenJwt);
}
