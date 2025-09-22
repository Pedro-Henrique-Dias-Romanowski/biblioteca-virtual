package com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class LivroInvalidoException extends BusinessException {
    public LivroInvalidoException(String message) {
        super(message);
    }
}
