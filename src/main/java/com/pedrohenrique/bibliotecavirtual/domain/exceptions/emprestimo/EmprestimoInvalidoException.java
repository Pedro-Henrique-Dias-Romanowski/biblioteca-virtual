package com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class EmprestimoInvalidoException extends BusinessException {
    public EmprestimoInvalidoException(String message) {
        super(message);
    }
}
