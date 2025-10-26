package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.controller.exceptions.GlobalExceptionHandler;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.LivroMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.AdministradorRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.TokenService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.LivroUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LivroController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LivroMapper livroMapper;

    @MockBean
    private LivroUseCase livroUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    ClienteRepository clienteRepository;

    @MockBean
    AdministradorRepository administradorRepository;

    private LivroRequestDTO livroRequestDTO;
    private Livro livroMock;
    private LivroResponseDTO livroResponseDTO;
    private List<Livro> livrosMock;
    private List<LivroResponseDTO> livrosResponseDTO;

    @BeforeEach
    void setUp() {
        livroRequestDTO = new LivroRequestDTO("Dom Casmurro", "Machado de Assis", "Romance brasileiro clássico", 2000, true);

        livroMock = new Livro();
        livroMock.setId(1L);
        livroMock.setTitulo("Dom Casmurro");
        livroMock.setAutor("Machado de Assis");
        livroMock.setEditora("Romance brasileiro clássico");
        livroMock.setAnoPublicacao(2000);
        livroMock.setDisponivel(true);

        livroResponseDTO = new LivroResponseDTO(1L, "Dom Casmurro", "Machado de Assis", "Romance brasileiro clássico", 2000, true);

        Livro livroMock2 = new Livro();
        livroMock2.setId(2L);
        livroMock2.setTitulo("Memórias Póstumas de Brás Cubas");
        livroMock2.setAutor("Machado de Assis");
        livroMock2.setEditora("Outro romance brasileiro clássico");
        livroMock2.setAnoPublicacao(2000);
        livroMock2.setDisponivel(true);

        LivroResponseDTO livroResponseDTO2 = new LivroResponseDTO(2L, "Memórias Póstumas de Brás Cubas", "Machado de Assis", "Outro romance brasileiro clássico", 2000, true);

        livrosMock = Arrays.asList(livroMock, livroMock2);
        livrosResponseDTO = Arrays.asList(livroResponseDTO, livroResponseDTO2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve cadastrar um livro com sucesso")
    void deveCadastrarLivroComSucesso() throws Exception {
        when(livroMapper.toDomain(any(LivroRequestDTO.class))).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(any(Livro.class))).thenReturn(livroMock);
        when(livroMapper.toResponse(any(Livro.class))).thenReturn(livroResponseDTO);

        mockMvc.perform(post("/cadastrar/livros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor").value("Machado de Assis"))
                .andExpect(jsonPath("$.disponivel").value(true));

        verify(livroUseCase, times(1)).cadastrarLivro(any(Livro.class));
        verify(livroMapper, times(1)).toResponse(any(Livro.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve lançar exceção ao cadastrar livro com dados inválidos")
    void deveLancarExcecaoAoCadastrarLivroComDadosInvalidos() throws Exception {
        when(livroMapper.toDomain(any(LivroRequestDTO.class))).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(any(Livro.class)))
                .thenThrow(new BusinessException("Erro ao cadastrar livro"));

        mockMvc.perform(post("/cadastrar/livros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro ao cadastrar livro"));

        verify(livroUseCase, times(1)).cadastrarLivro(any(Livro.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve lançar exceção ao cadastrar livro com título duplicado")
    void deveLancarExcecaoAoCadastrarLivroComTituloDuplicado() throws Exception {
        when(livroMapper.toDomain(any(LivroRequestDTO.class))).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(any(Livro.class)))
                .thenThrow(new BusinessException("Livro com este título já existe"));

        mockMvc.perform(post("/cadastrar/livros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Livro com este título já existe"));

        verify(livroUseCase, times(1)).cadastrarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve retornar todos os livros cadastrados")
    void deveRetornarTodosOsLivrosCadastrados() throws Exception {
        when(livroUseCase.visualizarTodosOsLivros()).thenReturn(livrosMock);
        when(livroMapper.toResponse(livrosMock.get(0))).thenReturn(livrosResponseDTO.get(0));
        when(livroMapper.toResponse(livrosMock.get(1))).thenReturn(livrosResponseDTO.get(1));

        mockMvc.perform(get("/livros")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(livroUseCase, times(1)).visualizarTodosOsLivros();
        verify(livroMapper, times(2)).toResponse(any(Livro.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver livros cadastrados")
    void deveRetornarListaVaziaQuandoNaoHouverLivrosCadastrados() throws Exception {
        when(livroUseCase.visualizarTodosOsLivros()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/livros")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(livroUseCase, times(1)).visualizarTodosOsLivros();
        verify(livroMapper, never()).toResponse(any(Livro.class));
    }

    @Test
    @DisplayName("Deve retornar livro quando buscar por ID existente")
    void deveRetornarLivroQuandoBuscarPorIdExistente() throws Exception {
        Long idLivro = 1L;
        when(livroUseCase.buscarLivroPorId(idLivro)).thenReturn(Optional.of(livroMock));
        when(livroMapper.toResponse(any(Livro.class))).thenReturn(livroResponseDTO);

        mockMvc.perform(get("/livros/{idLivro}", idLivro)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor").value("Machado de Assis"));

        verify(livroUseCase, times(1)).buscarLivroPorId(idLivro);
        verify(livroMapper, times(1)).toResponse(any(Livro.class));
    }

    @Test
    @DisplayName("Deve retornar no content quando buscar por ID inexistente")
    void deveRetornarNoContentQuandoBuscarPorIdInexistente() throws Exception {
        Long idLivro = 999L;
        when(livroUseCase.buscarLivroPorId(idLivro)).thenReturn(Optional.empty());

        mockMvc.perform(get("/livros/{idLivro}", idLivro)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(livroUseCase, times(1)).buscarLivroPorId(idLivro);
        verify(livroMapper, never()).toResponse(any(Livro.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve remover livro com sucesso")
    void deveRemoverLivroComSucesso() throws Exception {
        Long idLivro = 1L;
        doNothing().when(livroUseCase).removerLivro(idLivro);

        mockMvc.perform(delete("/livros/{idLivro}", idLivro)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(livroUseCase, times(1)).removerLivro(idLivro);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve lançar exceção ao tentar remover livro com empréstimo ativo")
    void deveLancarExcecaoAoRemoverLivroComEmprestimoAtivo() throws Exception {
        Long idLivro = 1L;
        doThrow(new BusinessException("Não é possível remover livro com empréstimo ativo"))
                .when(livroUseCase).removerLivro(idLivro);

        mockMvc.perform(delete("/livros/{idLivro}", idLivro)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Não é possível remover livro com empréstimo ativo"));

        verify(livroUseCase, times(1)).removerLivro(idLivro);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve lançar exceção ao tentar remover livro inexistente")
    void deveLancarExcecaoAoRemoverLivroInexistente() throws Exception {
        Long idLivro = 999L;
        doThrow(new BusinessException("Livro não encontrado"))
                .when(livroUseCase).removerLivro(idLivro);

        mockMvc.perform(delete("/livros/{idLivro}", idLivro)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Livro não encontrado"));

        verify(livroUseCase, times(1)).removerLivro(idLivro);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve validar que apenas administradores podem cadastrar livros")
    void deveValidarQueApenasAdministradoresPodemCadastrarLivros() throws Exception {
        when(livroMapper.toDomain(any(LivroRequestDTO.class))).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(any(Livro.class))).thenReturn(livroMock);
        when(livroMapper.toResponse(any(Livro.class))).thenReturn(livroResponseDTO);

        mockMvc.perform(post("/cadastrar/livros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestDTO)))
                .andExpect(status().isOk());

        verify(livroUseCase, times(1)).cadastrarLivro(any(Livro.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve validar que apenas administradores podem remover livros")
    void deveValidarQueApenasAdministradoresPodemRemoverLivros() throws Exception {
        Long idLivro = 1L;
        doNothing().when(livroUseCase).removerLivro(idLivro);

        mockMvc.perform(delete("/livros/{idLivro}", idLivro)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(livroUseCase, times(1)).removerLivro(idLivro);
    }

    @Test
    @DisplayName("Deve permitir que qualquer usuário visualize livros")
    void devePermitirQueQualquerUsuarioVisualizeLivros() throws Exception {
        when(livroUseCase.visualizarTodosOsLivros()).thenReturn(livrosMock);
        when(livroMapper.toResponse(any(Livro.class))).thenReturn(livroResponseDTO);

        mockMvc.perform(get("/livros")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(livroUseCase, times(1)).visualizarTodosOsLivros();
    }

    @Test
    @DisplayName("Deve permitir que qualquer usuário busque livro por ID")
    void devePermitirQueQualquerUsuarioBusqueLivroPorId() throws Exception {
        Long idLivro = 1L;
        when(livroUseCase.buscarLivroPorId(idLivro)).thenReturn(Optional.of(livroMock));
        when(livroMapper.toResponse(any(Livro.class))).thenReturn(livroResponseDTO);

        mockMvc.perform(get("/livros/{idLivro}", idLivro)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(livroUseCase, times(1)).buscarLivroPorId(idLivro);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve testar serialização completa do livro")
    void deveTestarSerializacaoCompletaDoLivro() throws Exception {
        when(livroMapper.toDomain(any(LivroRequestDTO.class))).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(any(Livro.class))).thenReturn(livroMock);
        when(livroMapper.toResponse(any(Livro.class))).thenReturn(livroResponseDTO);

        mockMvc.perform(post("/cadastrar/livros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").exists())
                .andExpect(jsonPath("$.autor").exists())
                .andExpect(jsonPath("$.editora").exists())
                .andExpect(jsonPath("$.anoPublicacao").exists())
                .andExpect(jsonPath("$.disponivel").exists());

        verify(livroUseCase, times(1)).cadastrarLivro(any(Livro.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve verificar que livro cadastrado está disponível por padrão")
    void deveVerificarQueLivroCadastradoEstaDisponivelPorPadrao() throws Exception {
        when(livroMapper.toDomain(any(LivroRequestDTO.class))).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(any(Livro.class))).thenReturn(livroMock);
        when(livroMapper.toResponse(any(Livro.class))).thenReturn(livroResponseDTO);

        mockMvc.perform(post("/cadastrar/livros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponivel").value(true));

        verify(livroUseCase, times(1)).cadastrarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve validar que endpoint de buscar todos os livros retorna lista")
    void deveValidarQueEndpointRetornaLista() throws Exception {
        when(livroUseCase.visualizarTodosOsLivros()).thenReturn(livrosMock);
        when(livroMapper.toResponse(any(Livro.class)))
                .thenReturn(livrosResponseDTO.get(0))
                .thenReturn(livrosResponseDTO.get(1));

        mockMvc.perform(get("/livros")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(livroUseCase, times(1)).visualizarTodosOsLivros();
    }
}

