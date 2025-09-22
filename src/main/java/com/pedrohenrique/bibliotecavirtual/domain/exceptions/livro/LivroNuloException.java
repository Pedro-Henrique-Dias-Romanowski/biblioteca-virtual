package com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class LivroNuloException extends BusinessException {
    public LivroNuloException(String message) {
        super(message);
    }
}
