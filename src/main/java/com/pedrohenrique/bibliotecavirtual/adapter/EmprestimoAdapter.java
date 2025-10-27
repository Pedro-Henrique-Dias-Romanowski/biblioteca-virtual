package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.EmprestimoEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.EmprestimoRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.EmailService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import org.mapstruct.ValueMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EmprestimoAdapter implements EmprestimoOutputPort {

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoMapper emprestimoMapper;
    private final EmailService emailService;
    private final ClienteRepository clienteRepository;
    private final Logger logger = LoggerFactory.getLogger(EmprestimoAdapter.class);

    @Value("${mensagem.emprestimo.email.felicitacoes}")
    private String mensagemEmprestimoEmailFelicitacoes;

    @Value("${mensagem.emprestimo.confirmado.sucesso}")
    private String mensagemEmprestimoConfirmadoSucesso;

    @Value("${mensagem.emprestimo.devolucao.email}")
    private String mensagemEmprestimoDevolucaoEmail;

    public EmprestimoAdapter(EmprestimoRepository emprestimoRepository, EmprestimoMapper emprestimoMapper, EmailService emailService, ClienteRepository clienteRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.emprestimoMapper = emprestimoMapper;
        this.emailService = emailService;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public Emprestimo realizarEmprestimo(Emprestimo emprestimo){
        var emprestimoEntity = emprestimoMapper.toEntity(emprestimo);
        emprestimoRepository.save(emprestimoEntity);
        var mensagemFelicitacoes = String.format(mensagemEmprestimoEmailFelicitacoes, extrairNomeLivrosEmprestimo(emprestimoEntity));
        emailService.enviarEmail(emprestimoEntity.getClienteId().getEmail(),mensagemEmprestimoConfirmadoSucesso, mensagemFelicitacoes);
        logger.info("O emprestimo foi confirmado com sucesso: ID: {}", emprestimo.getId());
        return emprestimoMapper.entityToDomain(emprestimoEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Emprestimo> visualizarTodosOsEmprestimos(Long idCliente){
        ClienteEntity clienteEntity = clienteRepository.findById(idCliente).orElse(null);
        return emprestimoRepository.findAllEmprestimosByClienteId(clienteEntity).stream().map(emprestimoMapper::entityToDomain)
                .toList();
    }

    @Override
    @Transactional
    public Emprestimo realizarDevolucaoEmprestimo(Emprestimo emprestimo) {
        var emprestimoEntity = emprestimoRepository.getReferenceById(emprestimo.getId());
        emprestimoEntity = emprestimoMapper.toEntity(emprestimo);
        emprestimoRepository.save(emprestimoEntity);
        var mensagemDevolucaoEmail = String.format(mensagemEmprestimoDevolucaoEmail, extrairNomeLivrosEmprestimo(emprestimoEntity));
        emailService.enviarEmail(emprestimoEntity.getClienteId().getEmail(), "Devolução empréstimo", mensagemDevolucaoEmail );
        return emprestimoMapper.entityToDomain(emprestimoEntity);
    }

    @Override
    public Boolean existsById(Long idEmprestimo) {
        return emprestimoRepository.existsById(idEmprestimo);
    }

    @Override
    public Emprestimo getReferenceById(Long idEmprestimo) {
       var emprestimoEntity = emprestimoRepository.getReferenceById(idEmprestimo);
         return emprestimoMapper.entityToDomain(emprestimoEntity);
    }


    private String extrairNomeLivrosEmprestimo(EmprestimoEntity emprestimoEntity){
        if (emprestimoEntity.getLivros() != null) {
            return emprestimoEntity.getLivros().stream()
                    .map(LivroEntity::getTitulo)
                    .collect(Collectors.joining(", "));
        }
        return "";
    }
}
