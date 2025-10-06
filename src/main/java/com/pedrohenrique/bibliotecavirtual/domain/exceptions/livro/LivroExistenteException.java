package com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class LivroExistenteException  extends BusinessException {
    public LivroExistenteException(String message) {
        super(message);
    }
}
