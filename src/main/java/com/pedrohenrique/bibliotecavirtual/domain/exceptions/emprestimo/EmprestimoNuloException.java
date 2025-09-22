package com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class EmprestimoNuloException extends BusinessException {
    public EmprestimoNuloException(String message) {
        super(message);
    }
}
