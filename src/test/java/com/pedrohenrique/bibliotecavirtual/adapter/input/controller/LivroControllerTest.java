package com.pedrohenrique.bibliotecavirtual.adapter.input.controller;

import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request.LivroRequestDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response.LivroResponseDTO;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.LivroMapper;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.LivroUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

    @Mock
    private LivroMapper livroMapper;

    @Mock
    private LivroUseCase livroUseCase;

    @InjectMocks
    private LivroController livroController;

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
        livroMock.setEditora("Editora XYZ");
        livroMock.setAnoPublicacao(2000);
        livroMock.setDisponivel(true);

        livroResponseDTO = new LivroResponseDTO(1L, "Dom Casmurro", "Machado de Assis", "Romance brasileiro clássico", 2000, true);

        Livro livroMock2 = new Livro();
        livroMock2.setId(2L);
        livroMock2.setTitulo("Memórias Póstumas de Brás Cubas");
        livroMock2.setAutor("Machado de Assis");
        livroMock2.setEditora("Editora ABC");
        livroMock2.setAnoPublicacao(2000);
        livroMock2.setDisponivel(true);

        LivroResponseDTO livroResponseDTO2 = new LivroResponseDTO(2L, "Memórias Póstumas de Brás Cubas", "Machado de Assis", "Outro romance brasileiro clássico", 2000, true);

        livrosMock = Arrays.asList(livroMock, livroMock2);
        livrosResponseDTO = Arrays.asList(livroResponseDTO, livroResponseDTO2);
    }

    @Test
    @DisplayName("Deve cadastrar um livro com sucesso")
    void deveCadastrarLivroComSucesso() throws DataBaseException {
        when(livroMapper.toDomain(livroRequestDTO)).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(livroMock)).thenReturn(livroMock);
        when(livroMapper.toResponse(livroMock)).thenReturn(livroResponseDTO);

        ResponseEntity<LivroResponseDTO> response = livroController.cadastrarLivro(livroRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(livroResponseDTO, response.getBody());
        verify(livroUseCase, times(1)).cadastrarLivro(livroMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar livro com dados inválidos")
    void deveLancarExcecaoAoCadastrarLivroComDadosInvalidos() throws BusinessException {
        when(livroMapper.toDomain(livroRequestDTO)).thenReturn(livroMock);
        when(livroUseCase.cadastrarLivro(livroMock)).thenThrow(new BusinessException("Erro ao cadastrar livro"));

        assertThrows(BusinessException.class, () -> {
            livroController.cadastrarLivro(livroRequestDTO);
        });

        verify(livroUseCase, times(1)).cadastrarLivro(livroMock);
    }

    @Test
    @DisplayName("Deve retornar todos os livros cadastrados")
    void deveRetornarTodosOsLivrosCadastrados() {
        when(livroUseCase.visualizarTodosOsLivros()).thenReturn(livrosMock);
        when(livroMapper.toResponse(livrosMock.get(0))).thenReturn(livrosResponseDTO.get(0));
        when(livroMapper.toResponse(livrosMock.get(1))).thenReturn(livrosResponseDTO.get(1));

        ResponseEntity<List<LivroResponseDTO>> response = livroController.visualizarTodosOsLivros();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(livrosResponseDTO.size(), response.getBody().size());
        assertEquals(livrosResponseDTO.get(0).titulo(), response.getBody().get(0).titulo());
        assertEquals(livrosResponseDTO.get(1).titulo(), response.getBody().get(1).titulo());
        verify(livroUseCase, times(1)).visualizarTodosOsLivros();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver livros cadastrados")
    void deveRetornarListaVaziaQuandoNaoHouverLivrosCadastrados() {
        when(livroUseCase.visualizarTodosOsLivros()).thenReturn(List.of());

        ResponseEntity<List<LivroResponseDTO>> response = livroController.visualizarTodosOsLivros();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(livroUseCase, times(1)).visualizarTodosOsLivros();
    }

    @Test
    @DisplayName("Deve retornar livro quando buscar por ID existente")
    void deveRetornarLivroQuandoBuscarPorIdExistente() {
        Long idLivro = 1L;
        when(livroUseCase.buscarLivroPorId(idLivro)).thenReturn(Optional.of(livroMock));
        when(livroMapper.toResponse(livroMock)).thenReturn(livroResponseDTO);

        ResponseEntity<Optional<LivroResponseDTO>> response = livroController.buscarLivroPorId(idLivro);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(livroResponseDTO.id(), response.getBody().get().id());
        assertEquals(livroResponseDTO.titulo(), response.getBody().get().titulo());
        verify(livroUseCase, times(1)).buscarLivroPorId(idLivro);
    }

    @Test
    @DisplayName("Deve retornar no content quando buscar por ID inexistente")
    void deveRetornarNoContentQuandoBuscarPorIdInexistente() {
        Long idLivro = 999L;
        when(livroUseCase.buscarLivroPorId(idLivro)).thenReturn(Optional.empty());

        ResponseEntity<Optional<LivroResponseDTO>> response = livroController.buscarLivroPorId(idLivro);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(livroUseCase, times(1)).buscarLivroPorId(idLivro);
    }

    @Test
    @DisplayName("Deve remover livro com sucesso")
    void deveRemoverLivroComSucesso() throws Exception, BusinessException {
        Long idLivro = 1L;
        doNothing().when(livroUseCase).removerLivro(idLivro);

        ResponseEntity<Void> response = livroController.removerLivro(idLivro);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(livroUseCase, times(1)).removerLivro(idLivro);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover livro com empréstimo ativo")
    void deveLancarExcecaoAoRemoverLivroComEmprestimoAtivo() throws Exception, BusinessException {
        Long idLivro = 1L;
        doThrow(new BusinessException("Não é possível remover livro com empréstimo ativo"))
                .when(livroUseCase).removerLivro(idLivro);

        assertThrows(BusinessException.class, () -> {
            livroController.removerLivro(idLivro);
        });

        verify(livroUseCase, times(1)).removerLivro(idLivro);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover livro inexistente")
    void deveLancarExcecaoAoRemoverLivroInexistente() throws Exception, BusinessException {
        Long idLivro = 999L;
        doThrow(new BusinessException("Livro não encontrado"))
                .when(livroUseCase).removerLivro(idLivro);

        assertThrows(BusinessException.class, () -> {
            livroController.removerLivro(idLivro);
        });

        verify(livroUseCase, times(1)).removerLivro(idLivro);
    }
}
