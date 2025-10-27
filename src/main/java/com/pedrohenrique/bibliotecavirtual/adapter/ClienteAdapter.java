package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.EmprestimoRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.service.EmailService;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class ClienteAdapter implements ClienteOutputPort {

    private final ClienteMapper clienteMapper;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String codigo = String.format("%04d", RandomUtils.nextInt(1, 10000));
    private final Logger logger = LoggerFactory.getLogger(ClienteAdapter.class);

    @Value("${mensagem.cliente.cadastrado.sucesso}")
    private String mensagemCadastradoSucesso;

    @Value("${mensagem.saudacoes.cliente}")
    private String mensagemSaudacoesCliente;

    @Value("${mensagem.cliente.esqueci.minha.senha.assunto}")
    private String mensagemEsqueciMinhaSenhaAssunto;

    @Value("${mensagem.cliente.esqueci.minha.senha.conteudo}")
    private String mensagemEsqueciMinhaSenhaConteudo;

    public ClienteAdapter(ClienteMapper clienteMapper, ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.clienteMapper = clienteMapper;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    @Override
    @Transactional
    public Cliente cadastrarCliente(Cliente cliente){
        var clienteEntity = clienteMapper.toEntity(cliente);
        var senhaCliente = clienteEntity.getSenha();
        clienteEntity.setSenha(passwordEncoder.encode(senhaCliente));
        var clienteEntitySalvo = clienteRepository.save(clienteEntity);
        emailService.enviarEmail(clienteEntitySalvo.getEmail(), mensagemCadastradoSucesso, String.format(mensagemCadastradoSucesso, clienteEntitySalvo.getNome()));
        logger.info("Cliente cadastrado com sucesso {}", cliente.getId());
        return clienteMapper.entityToDomain(clienteEntitySalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(Cliente cliente) {
        return clienteRepository.existsByEmailIgnoreCase(cliente.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return clienteRepository.existsById(id);
    }

    @Override
    public void esqueciMinhaSenha(String email) {
        emailService.enviarEmail(email, mensagemEsqueciMinhaSenhaAssunto, String.format(mensagemEsqueciMinhaSenhaConteudo, codigo));
    }

    @Override
    @Transactional
    public void alterarSenha(Integer codigo, String novaSenha, String confirmacaoNovaSenha, String email) {
        if (clienteRepository.existsByEmailIgnoreCase(email) && email != null){
            if (this.codigo.equals(String.format("%04d", codigo))){
                var clienteEntity = clienteRepository.findByEmailIgnoreCase(email).orElseThrow();
                clienteEntity.setSenha(passwordEncoder.encode(confirmacaoNovaSenha));
                clienteRepository.save(clienteEntity);
            }
        }
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id).map(clienteMapper::entityToDomain);
    }
}
