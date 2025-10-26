package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.usecase.validate.EmprestimoValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoUseCaseTest {

    @Mock
    private EmprestimoValidate emprestimoValidate;

    @Mock
    private EmprestimoOutputPort emprestimoOutputPort;

    @Mock
    private LivroOutputPort livroOutputPort;

    @InjectMocks
    private EmprestimoUseCase emprestimoUseCase;

    private Emprestimo emprestimo;
    private List<Emprestimo> emprestimos;
    private Livro livro1;
    private Livro livro2;

    @BeforeEach
    void setUp() {
        livro1 = new Livro();
        livro1.setId(1L);
        livro1.setTitulo("Livro 1");
        livro1.setDisponivel(true);

        livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("Livro 2");
        livro2.setDisponivel(true);

        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setClienteId(1L);
        emprestimo.setLivros(Arrays.asList(1L, 2L));
        emprestimo.setDataEmprestimo(null);
        emprestimo.setAtivo(null);

        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setId(2L);
        emprestimo2.setClienteId(1L);
        emprestimo2.setDataEmprestimo(LocalDate.now().minusDays(7));
        emprestimo2.setAtivo(true);

        emprestimos = Arrays.asList(emprestimo, emprestimo2);
    }

    @Test
    @DisplayName("Deve realizar empréstimo com sucesso")
    void deveRealizarEmprestimoComSucesso() {
        doNothing().when(emprestimoValidate).validarEmprestimo(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(emprestimoOutputPort.realizarEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoUseCase.realizarEmprestimo(emprestimo);

        assertNotNull(resultado);
        assertEquals(emprestimo.getId(), resultado.getId());
        assertEquals(LocalDate.now(), resultado.getDataEmprestimo());
        assertTrue(resultado.getAtivo());
        verify(emprestimoValidate, times(1)).validarEmprestimo(any(Emprestimo.class));
        verify(emprestimoOutputPort, times(1)).realizarEmprestimo(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de empréstimo falhar")
    void deveLancarExcecaoQuandoValidacaoEmprestimoFalhar() {
        doThrow(new BusinessException("Data de devolução inválida"))
                .when(emprestimoValidate).validarEmprestimo(any(Emprestimo.class));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            emprestimoUseCase.realizarEmprestimo(emprestimo);
        });

        assertEquals("Data de devolução inválida", exception.getMessage());
        verify(emprestimoValidate, times(1)).validarEmprestimo(any(Emprestimo.class));
        verify(emprestimoOutputPort, never()).realizarEmprestimo(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve definir data de empréstimo como data atual")
    void deveDefinirDataEmprestimoComoDataAtual() {
        doNothing().when(emprestimoValidate).validarEmprestimo(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(emprestimoOutputPort.realizarEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoUseCase.realizarEmprestimo(emprestimo);

        assertEquals(LocalDate.now(), resultado.getDataEmprestimo());
    }

    @Test
    @DisplayName("Deve definir empréstimo como ativo")
    void deveDefinirEmprestimoComoAtivo() {
        doNothing().when(emprestimoValidate).validarEmprestimo(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(emprestimoOutputPort.realizarEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoUseCase.realizarEmprestimo(emprestimo);

        assertTrue(resultado.getAtivo());
    }

    @Test
    @DisplayName("Deve visualizar todos os empréstimos de um cliente")
    void deveVisualizarTodosOsEmprestimosDeUmCliente() {
        when(emprestimoOutputPort.visualizarTodosOsEmprestimos(1L)).thenReturn(emprestimos);

        List<Emprestimo> resultado = emprestimoUseCase.visualizarTodosOsEmprestimos(1L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        verify(emprestimoOutputPort, times(1)).visualizarTodosOsEmprestimos(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não tiver empréstimos")
    void deveRetornarListaVaziaQuandoClienteNaoTiverEmprestimos() {
        when(emprestimoOutputPort.visualizarTodosOsEmprestimos(999L)).thenReturn(Collections.emptyList());

        List<Emprestimo> resultado = emprestimoUseCase.visualizarTodosOsEmprestimos(999L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(emprestimoOutputPort, times(1)).visualizarTodosOsEmprestimos(999L);
    }

    @Test
    @DisplayName("Deve realizar devolução de empréstimo com sucesso")
    void deveRealizarDevolucaoEmprestimoComSucesso() {
        emprestimo.setAtivo(true);
        doNothing().when(emprestimoValidate).validarDevolucao(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(emprestimoOutputPort.realizarDevolucaoEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoUseCase.realizarDevolucaoEmprestimo(emprestimo);

        assertNotNull(resultado);
        assertFalse(resultado.getAtivo());
        verify(emprestimoValidate, times(1)).validarDevolucao(any(Emprestimo.class));
        verify(livroOutputPort, times(1)).buscarLivroPorId(1L);
        verify(livroOutputPort, times(1)).buscarLivroPorId(2L);
        verify(emprestimoOutputPort, times(1)).realizarDevolucaoEmprestimo(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve marcar livros como disponíveis após devolução")
    void deveMarcarLivrosComoDisponiveisAposDevolucao() {
        emprestimo.setAtivo(true);
        livro1.setDisponivel(false);
        livro2.setDisponivel(false);
        doNothing().when(emprestimoValidate).validarDevolucao(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(emprestimoOutputPort.realizarDevolucaoEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        emprestimoUseCase.realizarDevolucaoEmprestimo(emprestimo);

        assertTrue(livro1.getDisponivel());
        assertTrue(livro2.getDisponivel());
        verify(livroOutputPort, times(2)).buscarLivroPorId(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de devolução falhar")
    void deveLancarExcecaoQuandoValidacaoDevolucaoFalhar() {
        emprestimo.setAtivo(true);
        doThrow(new BusinessException("Empréstimo não encontrado"))
                .when(emprestimoValidate).validarDevolucao(any(Emprestimo.class));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            emprestimoUseCase.realizarDevolucaoEmprestimo(emprestimo);
        });

        assertEquals("Empréstimo não encontrado", exception.getMessage());
        verify(emprestimoValidate, times(1)).validarDevolucao(any(Emprestimo.class));
        verify(emprestimoOutputPort, never()).realizarDevolucaoEmprestimo(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve definir empréstimo como inativo após devolução")
    void deveDefinirEmprestimoComoInativoAposDevolucao() {
        emprestimo.setAtivo(true);
        doNothing().when(emprestimoValidate).validarDevolucao(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(anyLong())).thenReturn(Optional.of(livro1));
        when(emprestimoOutputPort.realizarDevolucaoEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoUseCase.realizarDevolucaoEmprestimo(emprestimo);

        assertFalse(resultado.getAtivo());
    }

    @Test
    @DisplayName("Deve definir data de devolução como data atual")
    void deveDefinirDataDevolucaoComoDataAtual() {
        emprestimo.setAtivo(true);
        emprestimo.setDataDevolucao(null);
        doNothing().when(emprestimoValidate).validarDevolucao(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(emprestimoOutputPort.realizarDevolucaoEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoUseCase.realizarDevolucaoEmprestimo(emprestimo);

        assertNotNull(resultado.getDataDevolucao());
        assertEquals(LocalDate.now(), resultado.getDataDevolucao());
    }

    @Test
    @DisplayName("Deve marcar livros como indisponíveis após empréstimo")
    void deveMarcarLivrosComoIndisponiveisAposEmprestimo() {
        doNothing().when(emprestimoValidate).validarEmprestimo(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(emprestimoOutputPort.realizarEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        emprestimoUseCase.realizarEmprestimo(emprestimo);

        assertFalse(livro1.getDisponivel());
        assertFalse(livro2.getDisponivel());
        verify(livroOutputPort, times(2)).buscarLivroPorId(anyLong());
    }

    @Test
    @DisplayName("Deve processar devolução de múltiplos livros corretamente")
    void deveProcessarDevolucaoDeMultiplosLivrosCorretamente() {
        Livro livro3 = new Livro();
        livro3.setId(3L);
        livro3.setDisponivel(false);

        emprestimo.setLivros(Arrays.asList(1L, 2L, 3L));
        emprestimo.setAtivo(true);

        livro1.setDisponivel(false);
        livro2.setDisponivel(false);

        doNothing().when(emprestimoValidate).validarDevolucao(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(livroOutputPort.buscarLivroPorId(3L)).thenReturn(Optional.of(livro3));
        when(emprestimoOutputPort.realizarDevolucaoEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        emprestimoUseCase.realizarDevolucaoEmprestimo(emprestimo);

        assertTrue(livro1.getDisponivel());
        assertTrue(livro2.getDisponivel());
        assertTrue(livro3.getDisponivel());
        verify(livroOutputPort, times(3)).buscarLivroPorId(anyLong());
    }

    @Test
    @DisplayName("Deve processar empréstimo de múltiplos livros corretamente")
    void deveProcessarEmprestimoDeMultiplosLivrosCorretamente() {
        Livro livro3 = new Livro();
        livro3.setId(3L);
        livro3.setDisponivel(true);

        emprestimo.setLivros(Arrays.asList(1L, 2L, 3L));

        doNothing().when(emprestimoValidate).validarEmprestimo(any(Emprestimo.class));
        when(livroOutputPort.buscarLivroPorId(1L)).thenReturn(Optional.of(livro1));
        when(livroOutputPort.buscarLivroPorId(2L)).thenReturn(Optional.of(livro2));
        when(livroOutputPort.buscarLivroPorId(3L)).thenReturn(Optional.of(livro3));
        when(emprestimoOutputPort.realizarEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        emprestimoUseCase.realizarEmprestimo(emprestimo);

        assertFalse(livro1.getDisponivel());
        assertFalse(livro2.getDisponivel());
        assertFalse(livro3.getDisponivel());
        verify(livroOutputPort, times(3)).buscarLivroPorId(anyLong());
    }
}
