package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmprestimoValidate {

    private final LivroOutputPort livroOutputPort;

    public EmprestimoValidate(LivroOutputPort livroOutputPort) {
        this.livroOutputPort = livroOutputPort;
    }

    public boolean validarEmprestimo(Emprestimo emprestimo){
        return validarNulidade(emprestimo) &&
               validarDataDevolucaoLivro(emprestimo) &&
               validarIdCliente(emprestimo) &&
               validarIdsLivros(emprestimo);
    }

    private boolean validarNulidade(Emprestimo emprestimo){
        return emprestimo != null;
    }

    private boolean validarDataDevolucaoLivro(Emprestimo emprestimo){
        return emprestimo.getDataDevolucao().isAfter(emprestimo.getDataEmprestimo());
    }

    private boolean validarIdCliente(Emprestimo emprestimo){
        return emprestimo.getClienteId() != null;
    }

    private boolean validarIdsLivros(Emprestimo emprestimo){
        boolean livroValido = false;
        for (Long idLivro : emprestimo.getIdLivros()){
            var livro = livroOutputPort.pegarReferenciaPorId(idLivro);
            if (idLivro != null && livroOutputPort.existeLivroPorId(idLivro) && livro.getDisponivel()){
                livroValido = true;
            }
        }
        return livroValido;
    }
}
