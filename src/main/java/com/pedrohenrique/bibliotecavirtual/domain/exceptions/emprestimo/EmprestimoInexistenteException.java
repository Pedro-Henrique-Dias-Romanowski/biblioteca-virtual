package com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class EmprestimoInexistenteException extends BusinessException {
    public EmprestimoInexistenteException(String message) {
        super(message);
    }
}
