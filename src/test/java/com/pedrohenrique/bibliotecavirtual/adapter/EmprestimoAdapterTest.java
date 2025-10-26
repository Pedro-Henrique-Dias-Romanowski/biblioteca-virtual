package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.EmprestimoEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.EmprestimoRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.EmailService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoAdapterTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private EmprestimoAdapter emprestimoAdapter;

    private Emprestimo emprestimo;
    private EmprestimoEntity emprestimoEntity;
    private ClienteEntity clienteEntity;
    private List<LivroEntity> livrosEntity;
    private List<EmprestimoEntity> emprestimoEntityList;

    @BeforeEach
    void setUp() {
        // Configurar mensagens para injetar no adapter
        ReflectionTestUtils.setField(emprestimoAdapter, "mensagemEmprestimoEmailFelicitacoes",
                "Parabéns pelo empréstimo dos seguintes livros: %s");
        ReflectionTestUtils.setField(emprestimoAdapter, "mensagemEmprestimoConfirmadoSucesso",
                "Empréstimo confirmado com sucesso");

        // Configurar cliente
        clienteEntity = new ClienteEntity();
        clienteEntity.setId(1L);
        clienteEntity.setNome("João Silva");
        clienteEntity.setEmail("joao@email.com");

        // Configurar livros
        LivroEntity livro1 = new LivroEntity();
        livro1.setId(1L);
        livro1.setTitulo("Dom Casmurro");

        LivroEntity livro2 = new LivroEntity();
        livro2.setId(2L);
        livro2.setTitulo("Memórias Póstumas de Brás Cubas");

        livrosEntity = Arrays.asList(livro1, livro2);

        // Configurar empréstimo entity
        emprestimoEntity = new EmprestimoEntity();
        emprestimoEntity.setId(1L);
        emprestimoEntity.setDataEmprestimo(LocalDate.now());
        emprestimoEntity.setDataDevolucao(LocalDate.now().plusDays(7));
        emprestimoEntity.setAtivo(true);
        emprestimoEntity.setClienteId(clienteEntity);
        emprestimoEntity.setLivros(livrosEntity);

        // Configurar empréstimo domain
        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now().plusDays(7));
        emprestimo.setAtivo(false);
        emprestimo.setClienteId(1L);

        // Configurar lista de empréstimos para o cliente
        emprestimoEntityList = Collections.singletonList(emprestimoEntity);
    }

    @Test
    @DisplayName("Deve realizar empréstimo com sucesso")
    void deveRealizarEmprestimoComSucesso() {
        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        Emprestimo resultado = emprestimoAdapter.realizarEmprestimo(emprestimo);

        assertNotNull(resultado);
        assertEquals(emprestimo.getId(), resultado.getId());
        verify(emprestimoRepository, times(1)).save(emprestimoEntity);
        verify(emailService, times(1)).enviarEmail(
                eq("joao@email.com"),
                eq("Empréstimo confirmado com sucesso"),
                contains("Dom Casmurro, Memórias Póstumas de Brás Cubas"));
    }

    @Test
    @DisplayName("Deve visualizar todos os empréstimos do cliente")
    void deveVisualizarTodosOsEmprestimosDoCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEntity));
        when(emprestimoRepository.findAllEmprestimosByClienteId(clienteEntity)).thenReturn(emprestimoEntityList);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);

        List<Emprestimo> resultados = emprestimoAdapter.visualizarTodosOsEmprestimos(1L);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals(emprestimo.getId(), resultados.get(0).getId());
        verify(clienteRepository, times(1)).findById(1L);
        verify(emprestimoRepository, times(1)).findAllEmprestimosByClienteId(clienteEntity);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não tem empréstimos")
    void deveRetornarListaVaziaQuandoClienteNaoTemEmprestimos() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEntity));
        when(emprestimoRepository.findAllEmprestimosByClienteId(clienteEntity)).thenReturn(Collections.emptyList());

        List<Emprestimo> resultados = emprestimoAdapter.visualizarTodosOsEmprestimos(1L);

        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
        verify(clienteRepository, times(1)).findById(1L);
        verify(emprestimoRepository, times(1)).findAllEmprestimosByClienteId(clienteEntity);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não existe")
    void deveRetornarListaVaziaQuandoClienteNaoExiste() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        List<Emprestimo> resultados = emprestimoAdapter.visualizarTodosOsEmprestimos(999L);

        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
        verify(clienteRepository, times(1)).findById(999L);
        verify(emprestimoRepository, never()).findAllEmprestimosByClienteId(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Deve lidar com empréstimo sem livros")
    void deveLidarComEmprestimoSemLivros() {
        emprestimoEntity.setLivros(null);

        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        Emprestimo resultado = emprestimoAdapter.realizarEmprestimo(emprestimo);

        assertNotNull(resultado);
        verify(emailService, times(1)).enviarEmail(
                eq("joao@email.com"),
                eq("Empréstimo confirmado com sucesso"),
                eq("Parabéns pelo empréstimo dos seguintes livros: "));
    }

    @Test
    @DisplayName("Deve lidar com empréstimo com lista vazia de livros")
    void deveLidarComEmprestimoComListaVaziaDeLivros() {
        emprestimoEntity.setLivros(Collections.emptyList());

        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        Emprestimo resultado = emprestimoAdapter.realizarEmprestimo(emprestimo);

        assertNotNull(resultado);
        verify(emailService, times(1)).enviarEmail(
                eq("joao@email.com"),
                eq("Empréstimo confirmado com sucesso"),
                eq("Parabéns pelo empréstimo dos seguintes livros: "));
    }

    @Test
    @DisplayName("Deve realizar devolução de empréstimo com sucesso")
    void deveRealizarDevolucaoEmprestimoComSucesso() {
        ReflectionTestUtils.setField(emprestimoAdapter, "mensagemEmprestimoDevolucaoEmail",
                "Obrigado pela devolução dos seguintes livros: %s");

        emprestimo.setAtivo(false);
        emprestimoEntity.setAtivo(false);

        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        Emprestimo resultado = emprestimoAdapter.realizarDevolucaoEmprestimo(emprestimo);

        assertNotNull(resultado);
        assertEquals(emprestimo.getId(), resultado.getId());
        verify(emprestimoRepository, times(1)).save(emprestimoEntity);
        verify(emailService, times(1)).enviarEmail(
                eq("joao@email.com"),
                eq("Devolução empréstimo"),
                contains("Dom Casmurro, Memórias Póstumas de Brás Cubas"));
    }

    @Test
    @DisplayName("Deve realizar devolução de empréstimo sem livros")
    void deveRealizarDevolucaoEmprestimoSemLivros() {
        ReflectionTestUtils.setField(emprestimoAdapter, "mensagemEmprestimoDevolucaoEmail",
                "Obrigado pela devolução dos seguintes livros: %s");

        emprestimo.setAtivo(false);
        emprestimoEntity.setAtivo(false);
        emprestimoEntity.setLivros(null);

        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        Emprestimo resultado = emprestimoAdapter.realizarDevolucaoEmprestimo(emprestimo);

        assertNotNull(resultado);
        verify(emailService, times(1)).enviarEmail(
                eq("joao@email.com"),
                eq("Devolução empréstimo"),
                eq("Obrigado pela devolução dos seguintes livros: "));
    }

    @Test
    @DisplayName("Deve verificar se empréstimo existe por ID - retorna true")
    void deveVerificarSeEmprestimoExistePorIdRetornaTrue() {
        when(emprestimoRepository.existsById(1L)).thenReturn(true);

        Boolean resultado = emprestimoAdapter.existsById(1L);

        assertTrue(resultado);
        verify(emprestimoRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Deve verificar se empréstimo existe por ID - retorna false")
    void deveVerificarSeEmprestimoExistePorIdRetornaFalse() {
        when(emprestimoRepository.existsById(999L)).thenReturn(false);

        Boolean resultado = emprestimoAdapter.existsById(999L);

        assertFalse(resultado);
        verify(emprestimoRepository, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("Deve extrair corretamente os títulos dos livros concatenados")
    void deveExtrairCorretamenteTitulosDoLivrosConcatenados() {
        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        emprestimoAdapter.realizarEmprestimo(emprestimo);

        verify(emailService).enviarEmail(
                anyString(),
                anyString(),
                eq("Parabéns pelo empréstimo dos seguintes livros: Dom Casmurro, Memórias Póstumas de Brás Cubas"));
    }

    @Test
    @DisplayName("Deve visualizar múltiplos empréstimos do cliente")
    void deveVisualizarMultiplosEmprestimosDoCliente() {
        EmprestimoEntity emprestimoEntity2 = new EmprestimoEntity();
        emprestimoEntity2.setId(2L);
        emprestimoEntity2.setClienteId(clienteEntity);

        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setId(2L);

        List<EmprestimoEntity> multiplosEmprestimos = Arrays.asList(emprestimoEntity, emprestimoEntity2);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEntity));
        when(emprestimoRepository.findAllEmprestimosByClienteId(clienteEntity)).thenReturn(multiplosEmprestimos);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        when(emprestimoMapper.entityToDomain(emprestimoEntity2)).thenReturn(emprestimo2);

        List<Emprestimo> resultados = emprestimoAdapter.visualizarTodosOsEmprestimos(1L);

        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        assertEquals(1L, resultados.get(0).getId());
        assertEquals(2L, resultados.get(1).getId());
        verify(emprestimoMapper, times(2)).entityToDomain(any(EmprestimoEntity.class));
    }

    @Test
    @DisplayName("Deve enviar email com formato correto ao realizar empréstimo")
    void deveEnviarEmailComFormatoCorretoAoRealizarEmprestimo() {
        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        emprestimoAdapter.realizarEmprestimo(emprestimo);

        verify(emailService).enviarEmail(
                eq("joao@email.com"),
                eq("Empréstimo confirmado com sucesso"),
                argThat(mensagem ->
                    mensagem.contains("Dom Casmurro") &&
                    mensagem.contains("Memórias Póstumas de Brás Cubas")));
    }

    @Test
    @DisplayName("Deve enviar email com formato correto ao realizar devolução")
    void deveEnviarEmailComFormatoCorretoAoRealizarDevolucao() {
        ReflectionTestUtils.setField(emprestimoAdapter, "mensagemEmprestimoDevolucaoEmail",
                "Obrigado pela devolução dos seguintes livros: %s");

        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        emprestimoAdapter.realizarDevolucaoEmprestimo(emprestimo);

        verify(emailService).enviarEmail(
                eq("joao@email.com"),
                eq("Devolução empréstimo"),
                argThat(mensagem ->
                    mensagem.contains("Dom Casmurro") &&
                    mensagem.contains("Memórias Póstumas de Brás Cubas")));
    }


    @Test
    @DisplayName("Deve lidar com empréstimo com um único livro")
    void deveLidarComEmprestimoComUmUnicoLivro() {
        LivroEntity livroUnico = new LivroEntity();
        livroUnico.setId(1L);
        livroUnico.setTitulo("1984");
        emprestimoEntity.setLivros(Collections.singletonList(livroUnico));

        when(emprestimoMapper.toEntity(emprestimo)).thenReturn(emprestimoEntity);
        when(emprestimoRepository.save(emprestimoEntity)).thenReturn(emprestimoEntity);
        when(emprestimoMapper.entityToDomain(emprestimoEntity)).thenReturn(emprestimo);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        emprestimoAdapter.realizarEmprestimo(emprestimo);

        verify(emailService).enviarEmail(
                anyString(),
                anyString(),
                eq("Parabéns pelo empréstimo dos seguintes livros: 1984"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia corretamente quando buscar empréstimos de cliente sem empréstimos")
    void deveRetornarListaVaziaCorretamenteQuandoBuscarEmprestimosDeClienteSemEmprestimos() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEntity));
        when(emprestimoRepository.findAllEmprestimosByClienteId(clienteEntity)).thenReturn(Collections.emptyList());

        List<Emprestimo> resultados = emprestimoAdapter.visualizarTodosOsEmprestimos(1L);

        assertTrue(resultados.isEmpty());
        assertEquals(0, resultados.size());
    }
}


