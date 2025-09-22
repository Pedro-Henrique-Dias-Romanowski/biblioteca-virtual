package com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class ClienteExistenteException extends BusinessException {
    public ClienteExistenteException(String message) {
        super(message);
    }
}
