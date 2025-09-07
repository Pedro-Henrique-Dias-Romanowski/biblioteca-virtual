package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class ClienteAdapter implements ClienteOutputPort {

    private final ClienteMapper clienteMapper;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteAdapter(ClienteMapper clienteMapper, ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteMapper = clienteMapper;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public List<Emprestimo> visualizarTodosOsEmprestimos() {
        return List.of();
    }

    @Override
    @Transactional
    public Optional<Emprestimo> emprestarLivro(Emprestimo emprestimo) {
        return Optional.empty();
    }

    @Override
    @Transactional
    public Cliente cadastrarCliente(Cliente cliente){
        var clienteEntity = clienteMapper.toEntity(cliente);
        var senhaCliente = clienteEntity.getSenha();
        clienteEntity.setSenha(passwordEncoder.encode(senhaCliente));
        var clienteEntitySalvo = clienteRepository.save(clienteEntity);

        return clienteMapper.entityToDomain(clienteEntitySalvo);
    }

    @Override
    public boolean existsByEmail(Cliente cliente) {
        return clienteRepository.existsByEmailIgnoreCase(cliente.getEmail());
    }
}
