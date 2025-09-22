package com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class LivroNaoEcontradoException extends BusinessException {
    public LivroNaoEcontradoException(String message) {
        super(message);
    }
}
