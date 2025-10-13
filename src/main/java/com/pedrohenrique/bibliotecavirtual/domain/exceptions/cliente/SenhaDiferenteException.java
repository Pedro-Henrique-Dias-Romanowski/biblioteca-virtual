package com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class SenhaDiferenteException extends BusinessException {
    public SenhaDiferenteException(String message) {
        super(message);
    }
}
