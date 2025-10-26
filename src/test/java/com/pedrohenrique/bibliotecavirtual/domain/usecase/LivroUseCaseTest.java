package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.LivroValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroUseCaseTest {

    @Mock
    private LivroOutputPort livroOutputPort;

    @Mock
    private LivroValidate livroValidate;

    @InjectMocks
    private LivroUseCase livroUseCase;

    private Livro livro;
    private List<Livro> livros;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        livro.setEditora("Editora ABC");
        livro.setAnoPublicacao(2000);
        livro.setDisponivel(true);

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("Memórias Póstumas de Brás Cubas");
        livro2.setAutor("Machado de Assis");
        livro2.setEditora("Editora ABC");
        livro2.setAnoPublicacao(2000);
        livro2.setDisponivel(true);

        livros = Arrays.asList(livro, livro2);
    }

    @Test
    @DisplayName("Deve cadastrar livro com sucesso")
    void deveCadastrarLivroComSucesso() {
        doNothing().when(livroValidate).validarCadastroLivro(livro);
        when(livroOutputPort.cadastrarLivro(livro)).thenReturn(livro);

        Livro resultado = livroUseCase.cadastrarLivro(livro);

        assertNotNull(resultado);
        assertEquals(livro.getId(), resultado.getId());
        assertEquals(livro.getTitulo(), resultado.getTitulo());
        assertTrue(livro.getDisponivel());
        verify(livroValidate, times(1)).validarCadastroLivro(livro);
        verify(livroOutputPort, times(1)).cadastrarLivro(livro);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar livro com dados inválidos")
    void deveLancarExcecaoAoCadastrarLivroComDadosInvalidos() {
        doThrow(new BusinessException("Título não pode ser vazio"))
                .when(livroValidate).validarCadastroLivro(livro);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            livroUseCase.cadastrarLivro(livro);
        });

        assertEquals("Título não pode ser vazio", exception.getMessage());
        verify(livroValidate, times(1)).validarCadastroLivro(livro);
        verify(livroOutputPort, never()).cadastrarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve definir livro como disponível ao cadastrar")
    void deveDefinirLivroComoDisponivelAoCadastrar() {
        doNothing().when(livroValidate).validarCadastroLivro(livro);
        when(livroOutputPort.cadastrarLivro(livro)).thenReturn(livro);
        livro.setDisponivel(null);

        Livro resultado = livroUseCase.cadastrarLivro(livro);

        assertTrue(resultado.getDisponivel());
    }

    @Test
    @DisplayName("Deve visualizar todos os livros cadastrados")
    void deveVisualizarTodosOsLivrosCadastrados() {
        when(livroOutputPort.visualizaTodosOsLivros()).thenReturn(livros);

        List<Livro> resultado = livroUseCase.visualizarTodosOsLivros();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Dom Casmurro", resultado.get(0).getTitulo());
        assertEquals("Memórias Póstumas de Brás Cubas", resultado.get(1).getTitulo());
        verify(livroOutputPort, times(1)).visualizaTodosOsLivros();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver livros cadastrados")
    void deveRetornarListaVaziaQuandoNaoHouverLivrosCadastrados() {
        when(livroOutputPort.visualizaTodosOsLivros()).thenReturn(Collections.emptyList());

        List<Livro> resultado = livroUseCase.visualizarTodosOsLivros();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(livroOutputPort, times(1)).visualizaTodosOsLivros();
    }

    @Test
    @DisplayName("Deve buscar livro por ID existente")
    void deveBuscarLivroPorIdExistente() {
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));

        Optional<Livro> resultado = livroUseCase.buscarLivroPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Dom Casmurro", resultado.get().getTitulo());
        verify(livroOutputPort, times(1)).buscarLivroPorId(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando buscar por ID inexistente")
    void deveRetornarOptionalVazioQuandoBuscarPorIdInexistente() {
        when(livroOutputPort.buscarLivroPorId(999L)).thenReturn(Optional.empty());

        Optional<Livro> resultado = livroUseCase.buscarLivroPorId(999L);

        assertFalse(resultado.isPresent());
        verify(livroOutputPort, times(1)).buscarLivroPorId(999L);
    }

    @Test
    @DisplayName("Deve remover livro com sucesso")
    void deveRemoverLivroComSucesso() {
        doNothing().when(livroValidate).validarIdLivro(1L);
        doNothing().when(livroOutputPort).removerLivro(1L);

        livroUseCase.removerLivro(1L);

        verify(livroValidate, times(1)).validarIdLivro(1L);
        verify(livroOutputPort, times(1)).removerLivro(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover livro inválido")
    void deveLancarExcecaoAoTentarRemoverLivroInvalido() {
        doThrow(new BusinessException("Livro com empréstimo ativo não pode ser removido"))
                .when(livroValidate).validarIdLivro(1L);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            livroUseCase.removerLivro(1L);
        });

        assertEquals("Livro com empréstimo ativo não pode ser removido", exception.getMessage());
        verify(livroValidate, times(1)).validarIdLivro(1L);
        verify(livroOutputPort, never()).removerLivro(anyLong());
    }
}
