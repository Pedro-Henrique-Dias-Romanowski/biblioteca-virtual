package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.EmprestimoEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.EmprestimoRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.EmailService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import org.mapstruct.ValueMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class EmprestimoAdapter implements EmprestimoOutputPort {

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoMapper emprestimoMapper;
    private final EmailService emailService;

    @Value("${mensagem.emprestimo.email.felicitacoes}")
    private String mensagemEmprestimoEmailFelicitacoes;

    @Value("${mensagem.emprestimo.confirmado.sucesso}")
    private String mensagemEmprestimoConfirmadoSucesso;

    public EmprestimoAdapter(EmprestimoRepository emprestimoRepository, EmprestimoMapper emprestimoMapper, EmailService emailService) {
        this.emprestimoRepository = emprestimoRepository;
        this.emprestimoMapper = emprestimoMapper;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Emprestimo realizarEmprestimo(Emprestimo emprestimo){
        var emprestimoEntity = emprestimoMapper.toEntity(emprestimo);
        emprestimoRepository.save(emprestimoEntity);
        emailService.enviarEmail(emprestimoEntity.getClienteId().getEmail(),mensagemEmprestimoConfirmadoSucesso, mensagemEmprestimoEmailFelicitacoes + extrairNomeLivrosEmprestimo(emprestimoEntity));
        return emprestimoMapper.entityToDomain(emprestimoEntity);
    }

    @Override
    public List<Emprestimo> visualizarTodosOsEmprestimos() {
        return emprestimoRepository.findAll().stream().map(emprestimoMapper::entityToDomain)
                .toList();
    }

    private String extrairNomeLivrosEmprestimo(EmprestimoEntity emprestimoEntity){
        if (emprestimoEntity.getLivros() != null) {
            for (LivroEntity livroEntity : emprestimoEntity.getLivros()) {
                return livroEntity.getTitulo() + " ";
            }
        }
        return "";
    }
}
