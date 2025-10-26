package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroExistenteException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNaoEcontradoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNuloException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroValidateTest {

    @Mock
    private LivroOutputPort livroOutputPort;

    @InjectMocks
    private LivroValidate livroValidate;

    private Livro livro;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("O Senhor dos Anéis");
        livro.setAutor("J.R.R. Tolkien");
        livro.setEditora("HarperCollins");
        livro.setAnoPublicacao(1954);
        livro.setDisponivel(true);

        ReflectionTestUtils.setField(livroValidate, "mensagemErroLivroNulo", "Livro não pode ser nulo");
        ReflectionTestUtils.setField(livroValidate, "mensagemErroLivroNaoEcontrado", "Livro não encontrado: ");
        ReflectionTestUtils.setField(livroValidate, "mensagemErroLivroExistnte", "Livro já existe: ");
    }

    @Test
    @DisplayName("Deve validar livro com sucesso quando livro existe no banco de dados")
    void deveValidarLivroComSucessoQuandoLivroExisteNoBancoDeDados() {
        when(livroOutputPort.existsByTitulo(livro.getTitulo())).thenReturn(true);

        assertDoesNotThrow(() -> livroValidate.validarLivro(livro));

        verify(livroOutputPort).existsByTitulo(livro.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro é nulo ao validar livro")
    void deveLancarExcecaoQuandoLivroEhNuloAoValidarLivro() {
        LivroNuloException exception = assertThrows(
            LivroNuloException.class,
            () -> livroValidate.validarLivro(null)
        );

        assertEquals("Livro não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro não existe no banco de dados ao validar livro")
    void deveLancarExcecaoQuandoLivroNaoExisteNoBancoDeDadosAoValidarLivro() {
        when(livroOutputPort.existsByTitulo(livro.getTitulo())).thenReturn(false);

        LivroNaoEcontradoException exception = assertThrows(
            LivroNaoEcontradoException.class,
            () -> livroValidate.validarLivro(livro)
        );

        assertTrue(exception.getMessage().contains("Livro não encontrado: "));
        assertTrue(exception.getMessage().contains(livro.getTitulo()));
        verify(livroOutputPort).existsByTitulo(livro.getTitulo());
    }

    @Test
    @DisplayName("Deve validar ID do livro com sucesso quando ID não existe no banco de dados")
    void deveValidarIdDoLivroComSucessoQuandoIdNaoExisteNoBancoDeDados() {
        when(livroOutputPort.existsById(1L)).thenReturn(false);

        assertDoesNotThrow(() -> livroValidate.validarIdLivro(1L));

        verify(livroOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID do livro é nulo ao validar ID")
    void deveLancarExcecaoQuandoIdDoLivroEhNuloAoValidarId() {
        LivroNuloException exception = assertThrows(
            LivroNuloException.class,
            () -> livroValidate.validarIdLivro(null)
        );

        assertTrue(exception.getMessage().contains("Livro não pode ser nulo"));
        assertTrue(exception.getMessage().contains("ID: null"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID do livro já existe no banco de dados ao validar ID")
    void deveLancarExcecaoQuandoIdDoLivroJaExisteNoBancoDeDadosAoValidarId() {
        when(livroOutputPort.existsById(1L)).thenReturn(true);

        LivroNaoEcontradoException exception = assertThrows(
            LivroNaoEcontradoException.class,
            () -> livroValidate.validarIdLivro(1L)
        );

        assertTrue(exception.getMessage().contains("Livro não encontrado: "));
        assertTrue(exception.getMessage().contains("1"));
        verify(livroOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve validar cadastro de livro com sucesso quando livro não existe no banco de dados")
    void deveValidarCadastroComSucessoQuandoLivroNaoExisteNoBancoDeDados() {
        when(livroOutputPort.existsByTitulo(livro.getTitulo())).thenReturn(false);

        assertDoesNotThrow(() -> livroValidate.validarCadastroLivro(livro));

        verify(livroOutputPort).existsByTitulo(livro.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro é nulo ao validar cadastro")
    void deveLancarExcecaoQuandoLivroEhNuloAoValidarCadastro() {
        LivroNuloException exception = assertThrows(
            LivroNuloException.class,
            () -> livroValidate.validarCadastroLivro(null)
        );

        assertEquals("Livro não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro já existe no banco de dados ao validar cadastro")
    void deveLancarExcecaoQuandoLivroJaExisteNoBancoDeDadosAoValidarCadastro() {
        when(livroOutputPort.existsByTitulo(livro.getTitulo())).thenReturn(true);

        LivroExistenteException exception = assertThrows(
            LivroExistenteException.class,
            () -> livroValidate.validarCadastroLivro(livro)
        );

        assertTrue(exception.getMessage().contains("Livro já existe: "));
        assertTrue(exception.getMessage().contains(livro.getTitulo()));
        verify(livroOutputPort).existsByTitulo(livro.getTitulo());
    }

    @Test
    @DisplayName("Deve validar livro com todos os campos preenchidos corretamente")
    void deveValidarLivroComTodosCamposPreenchidosCorretamente() {
        livro.setTitulo("Clean Code");
        livro.setAutor("Robert C. Martin");
        livro.setEditora("Prentice Hall");
        livro.setAnoPublicacao(2008);
        livro.setDisponivel(true);

        when(livroOutputPort.existsByTitulo("Clean Code")).thenReturn(true);

        assertDoesNotThrow(() -> livroValidate.validarLivro(livro));

        verify(livroOutputPort).existsByTitulo("Clean Code");
    }
}

