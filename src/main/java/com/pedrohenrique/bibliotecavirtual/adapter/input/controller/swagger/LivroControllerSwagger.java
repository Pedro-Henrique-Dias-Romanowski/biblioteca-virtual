package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.swagger;


import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Livro", description = "Operações relacionadas a livros dentro da biblioteca virtual")
public interface LivroControllerSwagger {

    @Operation(summary = "Cadastrar um novo livro", description = "Permite o cadastro de um novo livro na biblioteca virtual")
    @PostMapping("/livros")
    // ESSA URL SÓ VAI SER ACESSADA PELO ADMIN
    public ResponseEntity<LivroResponseDTO> cadastrarLivro(@RequestBody LivroRequestDTO livroRequestDTO, @RequestHeader String tokenJwt);

    @Operation(summary = "Atualizar um livro existente", description = "Permite editar os dados de um livro já cadastrado na biblioteca virtual")
    @PutMapping("/livros/{idLivroAntigo}")
    // ESSA URL SÓ VAI SER ACESSADA PELO ADMIN
    public ResponseEntity<LivroResponseDTO> atualizarLivro(@PathVariable Long idLivroAntigo, @RequestBody LivroRequestDTO livroRequestDTO,
                                                           @RequestHeader String tokenJwt);

    @Operation(summary = "Buscar todos os livros", description = "Permite visualizar todos os livros disponíveis na biblioteca virtual")
    @GetMapping("/livros")
    // ESSA URL VAI SER ACESSADA TANTO PELO ADMIN QUANTO PELO CLIENTE, OS DOIS DEVEM ESTAR AUTENTICADOS
    public ResponseEntity<List<LivroResponseDTO>> visualizarTodosOsLivros(@RequestHeader String tokenJwt);

    @Operation(summary = "Buscar um livro por ID", description = "Faz a busca de um livro na biblioteca virtual utilizando seu ID")
    @GetMapping("/livros/{idLivro}")
    // ESSA URL VAI SER ACESSADA TANTO PELO ADMIN QUANTO PELO CLIENTE, OS DOIS DEVEM ESTAR AUTENTICADOS
    public ResponseEntity<Optional<LivroResponseDTO>> buscarLivro(@PathVariable Long idLivro, @RequestHeader String tokenJwt);

    @Operation(summary = "Remover um livro", description = "Permite a remoção de um livro da biblioteca virtual utilizando seu ID")
    @DeleteMapping("/livros/{idLivro}")
    // ESSA URL SÓ VAI SER ACESSADA PELO ADMIN
    public ResponseEntity<Void> removerLivro(@PathVariable Long idLivro, @RequestHeader String tokenJwt);
}
