package com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class DataEmprestimoInvalidoException extends BusinessException {
    public DataEmprestimoInvalidoException(String message) {
        super(message);
    }
}
