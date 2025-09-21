package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.EmprestimoMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.EmprestimoRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Emprestimo;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.EmprestimoOutputPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class EmprestimoAdapter implements EmprestimoOutputPort {

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoMapper emprestimoMapper;

    public EmprestimoAdapter(EmprestimoRepository emprestimoRepository, EmprestimoMapper emprestimoMapper) {
        this.emprestimoRepository = emprestimoRepository;
        this.emprestimoMapper = emprestimoMapper;
    }

    @Override
    @Transactional
    public Emprestimo realizarEmprestimo(Emprestimo emprestimo){
        var emprestimoEntity = emprestimoMapper.toEntity(emprestimo);
        emprestimoRepository.save(emprestimoEntity);
        return emprestimoMapper.entityToDomain(emprestimoEntity);
    }

    @Override
    public List<Emprestimo> visualizarTodosOsEmprestimos() {
        return emprestimoRepository.findAll().stream().map(emp -> emprestimoMapper.entityToDomain(emp))
                .toList();
    }
}
