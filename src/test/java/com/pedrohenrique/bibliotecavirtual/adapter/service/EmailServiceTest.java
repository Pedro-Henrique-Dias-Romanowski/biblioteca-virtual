package com.pedrohenrique.bibliotecavirtual.adapter.service;

import com.pedrohenrique.bibliotecavirtual.adapter.service.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private String emailDestinatario;
    private String assunto;
    private String conteudo;

    @BeforeEach
    void setUp() {
        emailDestinatario = "usuario@teste.com";
        assunto = "Teste de Email";
        conteudo = "Este é um conteúdo de teste para o email";
    }

    @Test
    @DisplayName("Deve enviar email com sucesso")
    void deveEnviarEmailComSucesso() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmail(emailDestinatario, assunto, conteudo);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer erro de mensagem")
    void deveLancarExcecaoQuandoOcorrerErroDeMessaging() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Erro ao enviar email")).when(mailSender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.enviarEmail(emailDestinatario, assunto, conteudo);
        });

        assertTrue(exception.getMessage().contains("Erro ao enviar email"));

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve lançar EmailException quando ocorre erro na criação da mensagem")
    void deveLancarEmailExceptionQuandoOcorreErroNaCriacaoDaMensagem() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MessagingException("Erro no formato da mensagem"))
                .when(mimeMessage).setSubject(anyString(), anyString());

        EmailException exception = assertThrows(EmailException.class, () -> {
            emailService.enviarEmail(emailDestinatario, assunto, conteudo);
        });

        assertTrue(exception.getMessage().contains("Erro ao enviar email"));
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
