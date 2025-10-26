package com.pedrohenrique.bibliotecavirtual.domain.usecase;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoUseCaseTest {

    @Mock
    private EmprestimoValidate emprestimoValidate;

    @Mock
    private EmprestimoOutputPort emprestimoOutputPort;

    @InjectMocks
    private EmprestimoUseCase emprestimoUseCase;

    private Emprestimo emprestimo;
    private List<Emprestimo> emprestimos;

    @BeforeEach
    void setUp() {
        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setClienteId(1L);
        emprestimo.setDataEmprestimo(null); // Será definido pelo use case
        emprestimo.setAtivo(null); // Será definido pelo use case

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
        when(emprestimoOutputPort.realizarEmprestimo(any(Emprestimo.class))).thenReturn(emprestimo);

        Emprestimo resultado = emprestimoUseCase.realizarEmprestimo(emprestimo);

        assertEquals(LocalDate.now(), resultado.getDataEmprestimo());
    }

    @Test
    @DisplayName("Deve definir empréstimo como ativo")
    void deveDefinirEmprestimoComoAtivo() {
        doNothing().when(emprestimoValidate).validarEmprestimo(any(Emprestimo.class));
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
}
