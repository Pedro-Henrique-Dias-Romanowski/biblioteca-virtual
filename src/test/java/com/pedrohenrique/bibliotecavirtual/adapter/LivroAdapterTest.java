package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.LivroMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.LivroRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroInvalidoException;
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
class LivroAdapterTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private LivroMapper livroMapper;

    @InjectMocks
    private LivroAdapter livroAdapter;

    private Livro livro;
    private LivroEntity livroEntity;
    private List<LivroEntity> livrosEntity;
    private List<Livro> livros;

    @BeforeEach
    void setUp() {
        // Configurar livro domain
        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        livro.setEditora("Editora XYZ");
        livro.setAnoPublicacao(2000);
        livro.setDisponivel(true);

        // Configurar livro entity
        livroEntity = new LivroEntity();
        livroEntity.setId(1L);
        livroEntity.setTitulo("Dom Casmurro");
        livroEntity.setAutor("Machado de Assis");
        livroEntity.setEditora("Editora XYZ");
        livroEntity.setAnoPublicacao(2000);
        livroEntity.setDisponivel(true);

        // Configurar lista de livros entity
        LivroEntity livroEntity2 = new LivroEntity();
        livroEntity2.setId(2L);
        livroEntity2.setTitulo("Memórias Póstumas de Brás Cubas");
        livroEntity2.setAutor("Machado de Assis");
        livroEntity2.setEditora("Editora 123");
        livroEntity2.setAnoPublicacao(2000);
        livroEntity2.setDisponivel(true);

        livrosEntity = Arrays.asList(livroEntity, livroEntity2);

        // Configurar lista de livros domain
        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("Memórias Póstumas de Brás Cubas");
        livro2.setAutor("Machado de Assis");
        livro2.setEditora("Editora 123");
        livro2.setAnoPublicacao(2000);
        livro2.setDisponivel(true);

        livros = Arrays.asList(livro, livro2);
    }

    @Test
    @DisplayName("Deve cadastrar livro com sucesso")
    void deveCadastrarLivroComSucesso() {
        when(livroMapper.toEntity(livro)).thenReturn(livroEntity);
        when(livroRepository.save(livroEntity)).thenReturn(livroEntity);
        when(livroMapper.entityToDomain(livroEntity)).thenReturn(livro);

        Livro resultado = livroAdapter.cadastrarLivro(livro);

        assertNotNull(resultado);
        assertEquals(livro.getId(), resultado.getId());
        assertEquals(livro.getTitulo(), resultado.getTitulo());
        verify(livroRepository, times(1)).save(livroEntity);
    }

    @Test
    @DisplayName("Deve visualizar todos os livros cadastrados")
    void deveVisualizarTodosOsLivrosCadastrados() {
        when(livroRepository.findAll()).thenReturn(livrosEntity);
        when(livroMapper.entityToDomain(livrosEntity.get(0))).thenReturn(livros.get(0));
        when(livroMapper.entityToDomain(livrosEntity.get(1))).thenReturn(livros.get(1));

        List<Livro> resultado = livroAdapter.visualizaTodosOsLivros();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(livroRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver livros cadastrados")
    void deveRetornarListaVaziaQuandoNaoHouverLivrosCadastrados() {
        when(livroRepository.findAll()).thenReturn(Collections.emptyList());

        List<Livro> resultado = livroAdapter.visualizaTodosOsLivros();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(livroRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar livro por ID existente")
    void deveBuscarLivroPorIdExistente() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livroEntity));
        when(livroMapper.entityToDomainOptional(Optional.of(livroEntity))).thenReturn(livro);

        Optional<Livro> resultado = livroAdapter.buscarLivroPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Dom Casmurro", resultado.get().getTitulo());
        verify(livroRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando buscar por ID inexistente")
    void deveRetornarOptionalVazioQuandoBuscarPorIdInexistente() {
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());
        when(livroMapper.entityToDomainOptional(Optional.empty())).thenReturn(null);

        Optional<Livro> resultado = livroAdapter.buscarLivroPorId(999L);

        assertFalse(resultado.isPresent());
        verify(livroRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve remover livro com sucesso")
    void deveRemoverLivroComSucesso() {
        doNothing().when(livroRepository).deleteById(1L);

        livroAdapter.removerLivro(1L);

        verify(livroRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve verificar se livro existe por ID")
    void deveVerificarSeLivroExistePorId() {
        when(livroRepository.existsById(1L)).thenReturn(true);
        when(livroRepository.existsById(999L)).thenReturn(false);

        boolean existeId1 = livroAdapter.existsById(1L);
        boolean existeId999 = livroAdapter.existsById(999L);

        assertTrue(existeId1);
        assertFalse(existeId999);
        verify(livroRepository, times(1)).existsById(1L);
        verify(livroRepository, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("Deve verificar se livro existe por título")
    void deveVerificarSeLivroExistePorTitulo() {
        when(livroRepository.findByTituloIgnoreCase("Dom Casmurro")).thenReturn(Optional.of(livroEntity));
        when(livroRepository.findByTituloIgnoreCase("Livro Inexistente")).thenReturn(Optional.empty());

        boolean existeTitulo1 = livroAdapter.existsByTitulo("Dom Casmurro");
        boolean existeTitulo2 = livroAdapter.existsByTitulo("Livro Inexistente");

        assertTrue(existeTitulo1);
        assertFalse(existeTitulo2);
        verify(livroRepository, times(1)).findByTituloIgnoreCase("Dom Casmurro");
        verify(livroRepository, times(1)).findByTituloIgnoreCase("Livro Inexistente");
    }

    @Test
    @DisplayName("Deve pegar referência de livro por ID existente")
    void devePegarReferenciaLivroPorIdExistente() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livroEntity));
        when(livroMapper.entityToDomain(livroEntity)).thenReturn(livro);

        Livro resultado = livroAdapter.pegarReferenciaPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Dom Casmurro", resultado.getTitulo());
        verify(livroRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pegar referência por ID inexistente")
    void deveLancarExcecaoAoTentarPegarReferenciaPorIdInexistente() {
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());

        LivroInvalidoException exception = assertThrows(LivroInvalidoException.class, () -> {
            livroAdapter.pegarReferenciaPorId(999L);
        });

        assertTrue(exception.getMessage().contains("Livro não encontrado"));
        verify(livroRepository, times(1)).findById(999L);
    }
}
