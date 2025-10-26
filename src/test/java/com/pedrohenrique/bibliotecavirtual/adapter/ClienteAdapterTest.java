package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.EmailService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteAdapterTest {

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ClienteAdapter clienteAdapter;

    private Cliente cliente;
    private ClienteEntity clienteEntity;
    private String codigoRecuperacao;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setSenha("senha123");

        clienteEntity = new ClienteEntity();
        clienteEntity.setId(1L);
        clienteEntity.setNome("João Silva");
        clienteEntity.setEmail("joao@email.com");
        clienteEntity.setSenha("senha_criptografada");

        codigoRecuperacao = "1234";

        ReflectionTestUtils.setField(clienteAdapter, "mensagemCadastradoSucesso", "Cliente cadastrado com sucesso!");
        ReflectionTestUtils.setField(clienteAdapter, "mensagemSaudacoesCliente", "Olá %s, seja bem-vindo!");
        ReflectionTestUtils.setField(clienteAdapter, "mensagemEsqueciMinhaSenhaAssunto", "Recuperação de senha");
        ReflectionTestUtils.setField(clienteAdapter, "mensagemEsqueciMinhaSenhaConteudo", "Seu código de recuperação é: %s");
        ReflectionTestUtils.setField(clienteAdapter, "codigo", codigoRecuperacao);
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        when(clienteMapper.toEntity(cliente)).thenReturn(clienteEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");
        when(clienteRepository.save(clienteEntity)).thenReturn(clienteEntity);
        when(clienteMapper.entityToDomain(clienteEntity)).thenReturn(cliente);
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        Cliente resultado = clienteAdapter.cadastrarCliente(cliente);

        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(cliente.getNome(), resultado.getNome());
        verify(clienteRepository, times(1)).save(clienteEntity);
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(emailService, times(1)).enviarEmail(eq("joao@email.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve verificar se existe cliente com o email informado")
    void deveVerificarSeExisteClienteComEmailInformado() {
        when(clienteRepository.existsByEmailIgnoreCase("joao@email.com")).thenReturn(true);

        boolean resultado = clienteAdapter.existsByEmail(cliente);

        assertTrue(resultado);
        verify(clienteRepository, times(1)).existsByEmailIgnoreCase("joao@email.com");
    }

    @Test
    @DisplayName("Deve verificar se não existe cliente com o email informado")
    void deveVerificarSeNaoExisteClienteComEmailInformado() {
        when(clienteRepository.existsByEmailIgnoreCase("joao@email.com")).thenReturn(false);

        boolean resultado = clienteAdapter.existsByEmail(cliente);

        assertFalse(resultado);
        verify(clienteRepository, times(1)).existsByEmailIgnoreCase("joao@email.com");
    }

    @Test
    @DisplayName("Deve verificar se existe cliente com o ID informado")
    void deveVerificarSeExisteClienteComIdInformado() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        boolean resultado = clienteAdapter.existsById(1L);

        assertTrue(resultado);
        verify(clienteRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Deve verificar se não existe cliente com o ID informado")
    void deveVerificarSeNaoExisteClienteComIdInformado() {
        when(clienteRepository.existsById(999L)).thenReturn(false);

        boolean resultado = clienteAdapter.existsById(999L);

        assertFalse(resultado);
        verify(clienteRepository, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha")
    void deveEnviarEmailRecuperacaoSenha() {
        String email = "joao@email.com";
        doNothing().when(emailService).enviarEmail(anyString(), anyString(), anyString());

        clienteAdapter.esqueciMinhaSenha(email);

        verify(emailService, times(1)).enviarEmail(eq(email), anyString(), contains(codigoRecuperacao));
    }

    @Test
    @DisplayName("Deve alterar senha do cliente com sucesso quando código correto e email existente")
    void deveAlterarSenhaClienteComSucessoQuandoCodigoCorretoEEmailExistente() {
        String novaSenha = "novaSenha123";
        String email = "joao@email.com";
        Integer codigo = 1234;

        when(clienteRepository.existsByEmailIgnoreCase(email)).thenReturn(true);
        when(clienteRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(clienteEntity));
        when(passwordEncoder.encode(novaSenha)).thenReturn("nova_senha_criptografada");
        when(clienteRepository.save(clienteEntity)).thenReturn(clienteEntity);

        clienteAdapter.alterarSenha(codigo, novaSenha, novaSenha, email);

        verify(clienteRepository, times(1)).findByEmailIgnoreCase(email);
        verify(passwordEncoder, times(1)).encode(novaSenha);
        verify(clienteRepository, times(1)).save(clienteEntity);
        assertEquals("nova_senha_criptografada", clienteEntity.getSenha());
    }

    @Test
    @DisplayName("Não deve alterar senha quando código incorreto")
    void naoDeveAlterarSenhaQuandoCodigoIncorreto() {
        String novaSenha = "novaSenha123";
        String email = "joao@email.com";
        Integer codigoIncorreto = 5678;

        when(clienteRepository.existsByEmailIgnoreCase(email)).thenReturn(true);

        clienteAdapter.alterarSenha(codigoIncorreto, novaSenha, novaSenha, email);

        verify(clienteRepository, never()).findByEmailIgnoreCase(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(clienteRepository, never()).save(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Não deve alterar senha quando email não existe")
    void naoDeveAlterarSenhaQuandoEmailNaoExiste() {
        String novaSenha = "novaSenha123";
        String email = "inexistente@email.com";
        Integer codigo = 1234;

        when(clienteRepository.existsByEmailIgnoreCase(email)).thenReturn(false);

        clienteAdapter.alterarSenha(codigo, novaSenha, novaSenha, email);

        verify(clienteRepository, never()).findByEmailIgnoreCase(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(clienteRepository, never()).save(any(ClienteEntity.class));
    }

    @Test
    @DisplayName("Não deve alterar senha quando email é nulo")
    void naoDeveAlterarSenhaQuandoEmailEhNulo() {
        String novaSenha = "novaSenha123";
        String email = null;
        Integer codigo = 1234;

        clienteAdapter.alterarSenha(codigo, novaSenha, novaSenha, email);

        verify(clienteRepository, never()).existsByEmailIgnoreCase(anyString());
        verify(clienteRepository, never()).findByEmailIgnoreCase(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(clienteRepository, never()).save(any(ClienteEntity.class));
    }
}
