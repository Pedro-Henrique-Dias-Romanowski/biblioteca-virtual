package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.DataEmprestimoInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.EmprestimoInexistenteException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.EmprestimoNuloException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNaoEcontradoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.QuantidadeMaximaLivrosEmprestimoException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoValidateTest {

    @Mock
    private LivroOutputPort livroOutputPort;

    @Mock
    private ClienteOutputPort clienteOutputPort;

    @Mock
    private EmprestimoOutputPort emprestimoOutputPort;

    @InjectMocks
    private EmprestimoValidate emprestimoValidate;

    private Emprestimo emprestimo;
    private Livro livro;

    @BeforeEach
    void setUp() {
        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setClienteId(1L);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now().plusDays(7));
        emprestimo.setLivros(List.of(1L));

        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");
        livro.setDisponivel(true);

        ReflectionTestUtils.setField(emprestimoValidate, "mensagemErroEmprestimoNulo", "Empréstimo não pode ser nulo");
        ReflectionTestUtils.setField(emprestimoValidate, "mensagemErroDataDevolucaoEmprestimo", "Data de devolução inválida");
        ReflectionTestUtils.setField(emprestimoValidate, "mensagemErroDadosClienteIdNulo", "ID do cliente não pode ser nulo");
        ReflectionTestUtils.setField(emprestimoValidate, "mensagemErroDadosClienteIdNaoEncontrado", "Cliente não encontrado");
        ReflectionTestUtils.setField(emprestimoValidate, "mensagemQuantidadeMaximaLivrosEmprestimos", "Quantidade máxima de livros excedida");
        ReflectionTestUtils.setField(emprestimoValidate, "mensagemErroLivroIndisponivel", "Livro indisponível");
        ReflectionTestUtils.setField(emprestimoValidate, "mensagemErroEmprestimoInexistente", "Empréstimo não encontrado");
    }

    @Test
    @DisplayName("Deve validar empréstimo com sucesso quando todos os dados estão corretos")
    void deveValidarEmprestimoComSucessoQuandoTodosDadosEstaoCorretos() {
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidate.validarEmprestimo(emprestimo));

        verify(livroOutputPort).buscarLivroPorId(1L);
        verify(livroOutputPort).existsById(1L);
        verify(clienteOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando empréstimo é nulo")
    void deveLancarExcecaoQuandoEmprestimoEhNulo() {
        EmprestimoNuloException exception = assertThrows(
            EmprestimoNuloException.class,
            () -> emprestimoValidate.validarEmprestimo(null)
        );

        assertEquals("Empréstimo não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de devolução é nula")
    void deveLancarExcecaoQuandoDataDevolucaoEhNula() {
        emprestimo.setDataDevolucao(null);

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);

        DataEmprestimoInvalidoException exception = assertThrows(
            DataEmprestimoInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Data de devolução inválida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de devolução é anterior à data de empréstimo")
    void deveLancarExcecaoQuandoDataDevolucaoEhAnteriorADataEmprestimo() {
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now().minusDays(1));

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);

        DataEmprestimoInvalidoException exception = assertThrows(
            DataEmprestimoInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Data de devolução inválida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de devolução ultrapassa 15 dias")
    void deveLancarExcecaoQuandoDataDevolucaoUltrapassa15Dias() {
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now().plusDays(16));

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);

        DataEmprestimoInvalidoException exception = assertThrows(
            DataEmprestimoInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Data de devolução inválida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando data de devolução é exatamente 15 dias após empréstimo")
    void deveValidarComSucessoQuandoDataDevolucaoEhExatamente15DiasAposEmprestimo() {
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now().plusDays(15));

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidate.validarEmprestimo(emprestimo));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID do cliente é nulo")
    void deveLancarExcecaoQuandoIdDoClienteEhNulo() {
        emprestimo.setClienteId(null);

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);

        ClienteInvalidoException exception = assertThrows(
            ClienteInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("ID do cliente não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não existe no banco de dados")
    void deveLancarExcecaoQuandoClienteNaoExisteNoBancoDeDados() {
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(false);

        ClienteInvalidoException exception = assertThrows(
            ClienteInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de livros é nula")
    void deveLancarExcecaoQuandoListaDeLivrosEhNula() {
        emprestimo.setLivros(null);

        LivroInvalidoException exception = assertThrows(
            LivroInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Livro indisponível", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de livros está vazia")
    void deveLancarExcecaoQuandoListaDeLivrosEstaVazia() {
        emprestimo.setLivros(Collections.emptyList());

        LivroInvalidoException exception = assertThrows(
            LivroInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Livro indisponível", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID do livro na lista é nulo")
    void deveLancarExcecaoQuandoIdDoLivroNaListaEhNulo() {
        emprestimo.setLivros(Collections.singletonList(null));

        LivroInvalidoException exception = assertThrows(
            LivroInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Livro indisponível", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro não é encontrado no banco de dados")
    void deveLancarExcecaoQuandoLivroNaoEhEncontradoNoBancoDeDados() {
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.empty());

        LivroInvalidoException exception = assertThrows(
            LivroInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Livro indisponível", exception.getMessage());
        verify(livroOutputPort).buscarLivroPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro não existe por ID")
    void deveLancarExcecaoQuandoLivroNaoExistePorId() {
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(false);

        LivroNaoEcontradoException exception = assertThrows(
            LivroNaoEcontradoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Livro indisponível", exception.getMessage());
        verify(livroOutputPort).buscarLivroPorId(1L);
        verify(livroOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando livro não está disponível")
    void deveLancarExcecaoQuandoLivroNaoEstaDisponivel() {
        livro.setDisponivel(false);
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);

        LivroInvalidoException exception = assertThrows(
            LivroInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Livro indisponível", exception.getMessage());
        verify(livroOutputPort).buscarLivroPorId(1L);
        verify(livroOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade de livros excede o máximo permitido")
    void deveLancarExcecaoQuandoQuantidadeDeLivrosExcedeOMaximoPermitido() {
        emprestimo.setLivros(Arrays.asList(1L, 2L, 3L, 4L));

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setDisponivel(true);

        Livro livro3 = new Livro();
        livro3.setId(3L);
        livro3.setDisponivel(true);

        Livro livro4 = new Livro();
        livro4.setId(4L);
        livro4.setDisponivel(true);

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(livroOutputPort.buscarLivroPorId(3L)).thenReturn(Optional.of(livro3));
        when(livroOutputPort.buscarLivroPorId(4L)).thenReturn(Optional.of(livro4));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(livroOutputPort.existsById(2L)).thenReturn(true);
        when(livroOutputPort.existsById(3L)).thenReturn(true);
        when(livroOutputPort.existsById(4L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        QuantidadeMaximaLivrosEmprestimoException exception = assertThrows(
            QuantidadeMaximaLivrosEmprestimoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Quantidade máxima de livros excedida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando quantidade de livros é exatamente o máximo permitido")
    void deveValidarComSucessoQuandoQuantidadeDeLivrosEhExatamenteOMaximoPermitido() {
        List<Long> livrosIds = Arrays.asList(1L, 2L, 3L);
        emprestimo.setLivros(livrosIds);

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setDisponivel(true);

        Livro livro3 = new Livro();
        livro3.setId(3L);
        livro3.setDisponivel(true);

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(livroOutputPort.buscarLivroPorId(3L)).thenReturn(Optional.of(livro3));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(livroOutputPort.existsById(2L)).thenReturn(true);
        when(livroOutputPort.existsById(3L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidate.validarEmprestimo(emprestimo));

        verify(livroOutputPort).buscarLivroPorId(1L);
        verify(livroOutputPort).buscarLivroPorId(2L);
        verify(livroOutputPort).buscarLivroPorId(3L);
    }

    @Test
    @DisplayName("Deve validar com sucesso quando data de devolução é igual à data de empréstimo")
    void deveValidarComSucessoQuandoDataDevolucaoEhIgualADataEmprestimo() {
        LocalDate hoje = LocalDate.now();
        emprestimo.setDataEmprestimo(hoje);
        emprestimo.setDataDevolucao(hoje);

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidate.validarEmprestimo(emprestimo));
    }

    @Test
    @DisplayName("Deve validar todos os livros da lista quando há múltiplos livros")
    void deveValidarTodosOsLivrosDaListaQuandoHaMultiplosLivros() {
        List<Long> livrosIds = Arrays.asList(1L, 2L);
        emprestimo.setLivros(livrosIds);

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setDisponivel(true);

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(livroOutputPort.existsById(2L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidate.validarEmprestimo(emprestimo));

        verify(livroOutputPort).buscarLivroPorId(1L);
        verify(livroOutputPort).buscarLivroPorId(2L);
        verify(livroOutputPort).existsById(1L);
        verify(livroOutputPort).existsById(2L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando segundo livro da lista não está disponível")
    void deveLancarExcecaoQuandoSegundoLivroDaListaNaoEstaDisponivel() {
        List<Long> livrosIds = Arrays.asList(1L, 2L);
        emprestimo.setLivros(livrosIds);

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setDisponivel(false);

        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(livroOutputPort.existsById(1L)).thenReturn(true);
        when(livroOutputPort.existsById(2L)).thenReturn(true);

        LivroInvalidoException exception = assertThrows(
            LivroInvalidoException.class,
            () -> emprestimoValidate.validarEmprestimo(emprestimo)
        );

        assertEquals("Livro indisponível", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar devolução com sucesso quando todos os dados estão corretos")
    void deveValidarDevolucaoComSucessoQuandoTodosDadosEstaoCorretos() {
        when(emprestimoOutputPort.existsById(1L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidate.validarDevolucao(emprestimo));

        verify(emprestimoOutputPort).existsById(1L);
        verify(clienteOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar devolução quando empréstimo é nulo")
    void deveLancarExcecaoAoValidarDevolucaoQuandoEmprestimoEhNulo() {
        EmprestimoNuloException exception = assertThrows(
            EmprestimoNuloException.class,
            () -> emprestimoValidate.validarDevolucao(null)
        );

        assertEquals("Empréstimo não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar devolução quando empréstimo não existe")
    void deveLancarExcecaoAoValidarDevolucaoQuandoEmprestimoNaoExiste() {
        when(emprestimoOutputPort.existsById(1L)).thenReturn(false);

        EmprestimoInexistenteException exception = assertThrows(
            EmprestimoInexistenteException.class,
            () -> emprestimoValidate.validarDevolucao(emprestimo)
        );

        assertEquals("Empréstimo não encontrado", exception.getMessage());
        verify(emprestimoOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar devolução quando ID do cliente é nulo")
    void deveLancarExcecaoAoValidarDevolucaoQuandoIdDoClienteEhNulo() {
        emprestimo.setClienteId(null);
        when(emprestimoOutputPort.existsById(1L)).thenReturn(true);

        ClienteInvalidoException exception = assertThrows(
            ClienteInvalidoException.class,
            () -> emprestimoValidate.validarDevolucao(emprestimo)
        );

        assertEquals("ID do cliente não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar devolução quando cliente não existe")
    void deveLancarExcecaoAoValidarDevolucaoQuandoClienteNaoExiste() {
        when(emprestimoOutputPort.existsById(1L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(false);

        ClienteInvalidoException exception = assertThrows(
            ClienteInvalidoException.class,
            () -> emprestimoValidate.validarDevolucao(emprestimo)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteOutputPort).existsById(1L);
    }

    @Test
    @DisplayName("Deve validar devolução quando empréstimo existe com ID válido")
    void deveValidarDevolucaoQuandoEmprestimoExisteComIdValido() {
        emprestimo.setId(999L);
        when(emprestimoOutputPort.existsById(999L)).thenReturn(true);
        when(clienteOutputPort.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> emprestimoValidate.validarDevolucao(emprestimo));

        verify(emprestimoOutputPort).existsById(999L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar devolução quando empréstimo com ID diferente não existe")
    void deveLancarExcecaoAoValidarDevolucaoQuandoEmprestimoComIdDiferenteNaoExiste() {
        emprestimo.setId(999L);
        when(emprestimoOutputPort.existsById(999L)).thenReturn(false);

        EmprestimoInexistenteException exception = assertThrows(
            EmprestimoInexistenteException.class,
            () -> emprestimoValidate.validarDevolucao(emprestimo)
        );

        assertEquals("Empréstimo não encontrado", exception.getMessage());
    }
}

