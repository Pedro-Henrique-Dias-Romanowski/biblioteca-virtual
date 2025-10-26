package com.pedrohenrique.bibliotecavirtual.domain.usecase.validate;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.cliente.ClienteInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.DataEmprestimoInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.EmprestimoInexistenteException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.emprestimo.EmprestimoNuloException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNaoEcontradoException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.QuantidadeMaximaLivrosEmprestimoException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import com.pedrohenrique.bibliotecavirtual.domain.utils.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmprestimoValidate {

    private final LivroOutputPort livroOutputPort;

    private final ClienteOutputPort clienteOutputPort;

    private final EmprestimoOutputPort emprestimoOutputPort;

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

    @Value("${mensagem.erro.emprestimo.inexistente")
    private String mensagemErroEmprestimoInexistente;

    public EmprestimoValidate(LivroOutputPort livroOutputPort, ClienteOutputPort clienteOutputPort, EmprestimoOutputPort emprestimoOutputPort, EmprestimoOutputPort emprestimoOutputPort1) {
        this.livroOutputPort = livroOutputPort;
        this.clienteOutputPort = clienteOutputPort;
        this.emprestimoOutputPort = emprestimoOutputPort1;
    }

    public void validarEmprestimo(Emprestimo emprestimo){
        validarNulidadeEmprestimo(emprestimo);
        validarLivros(emprestimo);
        validarDataDevolucaoLivro(emprestimo);
        validarNulidadeIdCliente(emprestimo);
        validarExistenciaIdCliente(emprestimo);
        validarQuantidadesLivrosEmprestimo(emprestimo);
    }

    public void validarDevolucao(Emprestimo emprestimo){
        validarNulidadeEmprestimo(emprestimo);
        validarExistenciaEmprestimo(emprestimo);
        validarNulidadeIdCliente(emprestimo);
        validarExistenciaIdCliente(emprestimo);
    }

    private void validarNulidadeEmprestimo(Emprestimo emprestimo){
        if (emprestimo == null){
            throw new EmprestimoNuloException(mensagemErroEmprestimoNulo);
        }
    }

    private void validarDataDevolucaoLivro(Emprestimo emprestimo){
        if (emprestimo.getDataDevolucao() == null)
            throw new DataEmprestimoInvalidoException(mensagemErroDataDevolucaoEmprestimo);
        if (emprestimo.getDataDevolucao().isBefore(emprestimo.getDataEmprestimo()))
            throw new DataEmprestimoInvalidoException(mensagemErroDataDevolucaoEmprestimo);
        if (emprestimo.getDataDevolucao().isAfter(emprestimo.getDataEmprestimo().plusDays(15))){
            throw new DataEmprestimoInvalidoException(mensagemErroDataDevolucaoEmprestimo);
        }
    }

    private void validarNulidadeIdCliente(Emprestimo emprestimo){
        if (emprestimo.getClienteId() == null )
            throw new ClienteInvalidoException(mensagemErroDadosClienteIdNulo);

    }

    private void validarExistenciaIdCliente(Emprestimo emprestimo){
        if (!clienteOutputPort.existsById(emprestimo.getClienteId()))
            throw new ClienteInvalidoException(mensagemErroDadosClienteIdNaoEncontrado);
    }

    private void validarExistenciaEmprestimo(Emprestimo emprestimo){
        if (!emprestimoOutputPort.existsById(emprestimo.getId())){
            throw new EmprestimoInexistenteException(mensagemErroEmprestimoInexistente);
        }
    }

    private void validarLivros(Emprestimo emprestimo){
        if (emprestimo.getLivros() == null || emprestimo.getLivros().isEmpty()){
            throw new LivroInvalidoException(mensagemErroLivroIndisponivel);
        } else {
            for (Long idLivro : emprestimo.getLivros()){
                var livro = livroOutputPort.buscarLivroPorId(idLivro);
                if (idLivro == null){
                    throw new LivroInvalidoException(mensagemErroLivroIndisponivel);
                }
                if (livro.isEmpty()){
                    throw new LivroInvalidoException(mensagemErroLivroIndisponivel);
                }
                if (!livroOutputPort.existsById(idLivro)){
                    throw new LivroNaoEcontradoException(mensagemErroLivroIndisponivel);
                }
                if (!livro.get().getDisponivel()){
                    throw new LivroInvalidoException(mensagemErroLivroIndisponivel );
                }
            }
        }
    }


    private void validarQuantidadesLivrosEmprestimo(Emprestimo emprestimo){
        if (((emprestimo.getLivros().size() > Constantes.QUANTIDADE_MAX_NUMERO_EMPRESTIMO)))
            throw new QuantidadeMaximaLivrosEmprestimoException(mensagemQuantidadeMaximaLivrosEmprestimos);
    }
}
