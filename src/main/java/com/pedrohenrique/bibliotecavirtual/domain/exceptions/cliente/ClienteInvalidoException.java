package com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class ClienteInvalidoException extends BusinessException {
    public ClienteInvalidoException(String message) {
        super(message);
    }
}
