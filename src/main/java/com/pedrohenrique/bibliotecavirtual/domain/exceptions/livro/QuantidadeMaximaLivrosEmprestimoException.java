package com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;

public class QuantidadeMaximaLivrosEmprestimoException extends BusinessException {
    public QuantidadeMaximaLivrosEmprestimoException(String message) {
        super(message);
    }
}
