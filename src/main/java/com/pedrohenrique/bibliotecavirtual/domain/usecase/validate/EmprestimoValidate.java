package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.DataEmprestimoInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.EmprestimoNuloException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.QuantidadeMaximaLivrosEmprestimoException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.utils.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmprestimoValidate {

    private final LivroOutputPort livroOutputPort;

    private final ClienteOutputPort clienteOutputPort;

    @Value(value = "${mensagem.erro.emprestimo.nulo}")
    private String mensagemErroEmprestimoNulo;

    @Value(value = "${mensagem.data.devolucao.emprestimo.invalida}")
    private String mensagemErroDataDevolucaoEmprestimo;

    @Value(value = "${mensagem.erro.dados.cliente.id.nulo}")
    private String mensagemErroDadosClienteIdNulo;

    @Value("${mensagem.erro.dados.cliente.id.nao.encontrado}")
    private String mensagemErroDadosClienteIdNaoEncontrado;

    @Value("${mensagem.quantidade.maxima.livros.emprestimo}")
    private String mensagemQuantidadeMaximaLivrosEmprestimos;

    @Value("${mensagem.erro.livro.indisponivel}")
    private String mensagemErroLivroIndisponivel;

    public EmprestimoValidate(LivroOutputPort livroOutputPort,ClienteOutputPort clienteOutputPort) {
        this.livroOutputPort = livroOutputPort;
        this.clienteOutputPort = clienteOutputPort;
    }

    public void validarEmprestimo(Emprestimo emprestimo){
        validarNulidadeEmprestimo(emprestimo);
        validarDataDevolucaoLivro(emprestimo);
        validarNulidadeIdCliente(emprestimo);
        validarExistenciaIdCliente(emprestimo);
        validarIdsLivros(emprestimo);
        validarQuantidadesLivrosEmprestimo(emprestimo);
    }

    private void validarNulidadeEmprestimo(Emprestimo emprestimo){
        if (emprestimo == null){
            throw new EmprestimoNuloException(mensagemErroEmprestimoNulo);
        }
    }

    private void validarDataDevolucaoLivro(Emprestimo emprestimo){
        if (emprestimo.getDataDevolucao().isBefore(emprestimo.getDataEmprestimo()))
            throw new DataEmprestimoInvalidoException(mensagemErroDataDevolucaoEmprestimo);
    }

    private void validarNulidadeIdCliente(Emprestimo emprestimo){
        if (emprestimo.getClienteId() == null )
            throw new ClienteInvalidoException(mensagemErroDadosClienteIdNulo);

    }

    private void validarExistenciaIdCliente(Emprestimo emprestimo){
        if (!clienteOutputPort.existsById(emprestimo.getClienteId()))
            throw new ClienteInvalidoException(mensagemErroDadosClienteIdNaoEncontrado);
    }

    private void validarIdsLivros(Emprestimo emprestimo) {
        for (Long idLivro : emprestimo.getIdLivros()) {
            if (idLivro == null || !livroOutputPort.existsById(idLivro)) {
                throw new LivroInvalidoException(mensagemErroLivroIndisponivel + " ID: " + idLivro);
            }
            var livro = livroOutputPort.pegarReferenciaPorId(idLivro);
            if (!livro.getDisponivel()) {
                throw new LivroInvalidoException(mensagemErroLivroIndisponivel + " ID: " + livro.getId());
            }
        }
    }


    private void validarQuantidadesLivrosEmprestimo(Emprestimo emprestimo){
        if (((emprestimo.getIdLivros().size() > Constantes.QUANTIDADE_MAX_NUMERO_EMPRESTIMO)))
            throw new QuantidadeMaximaLivrosEmprestimoException(mensagemQuantidadeMaximaLivrosEmprestimos);
    }
}
