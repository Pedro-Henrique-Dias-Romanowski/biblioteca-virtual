package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.ClienteMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.EmprestimoEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.ClienteRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.EmprestimoRepository;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.LivroRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.ClienteOutputPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClienteAdapter implements ClienteOutputPort {

    private final ClienteMapper clienteMapper;
    private final EmprestimoMapper emprestimoMapper;
    private final ClienteRepository clienteRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmprestimoAdapter emprestimoAdapter;

    public ClienteAdapter(ClienteMapper clienteMapper, EmprestimoMapper emprestimoMapper, ClienteRepository clienteRepository, EmprestimoRepository emprestimoRepository, PasswordEncoder passwordEncoder, EmprestimoAdapter emprestimoAdapter) {
        this.clienteMapper = clienteMapper;
        this.emprestimoMapper = emprestimoMapper;
        this.clienteRepository = clienteRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emprestimoAdapter = emprestimoAdapter;
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

    @Override
    public boolean existsById(Long id) {
        return clienteRepository.existsById(id);
    }
}
