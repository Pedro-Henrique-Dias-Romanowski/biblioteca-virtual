package com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class EmailClienteInvalidoException extends BusinessException {
    public EmailClienteInvalidoException(String message) {
        super(message);
    }
}
