package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.LivroMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.LivroRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class LivroAdapter implements LivroOutputPort {

    private final LivroRepository livroRepository;

    private final LivroMapper livroMapper;

    private final Logger logger = LoggerFactory.getLogger(LivroAdapter.class);

    public LivroAdapter(LivroRepository livroRepository, LivroMapper livroMapper) {
        this.livroRepository = livroRepository;
        this.livroMapper = livroMapper;
    }

    @Override
    @Transactional
    public Livro cadastrarLivro(Livro livro){
        var livroEntity = livroMapper.toEntity(livro);
        var livroSalvo = livroRepository.save(livroEntity);
        logger.info("Livro do id {} cadastrado com sucesso", livro.getId());
        return livroMapper.entityToDomain(livroSalvo);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Livro> visualizaTodosOsLivros() {
        return livroRepository.findAll()
                .stream()
                .map(livroMapper::entityToDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Livro> buscarLivroPorId(Long idLivro){
        var livroEntity = livroRepository.findById(idLivro);
        return Optional.ofNullable(livroMapper.entityToDomainOptional(livroEntity));
    }

    @Override
    @Transactional
    public void removerLivro(Long idLivro) {
        livroRepository.deleteById(idLivro);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long idLivro) {
        return livroRepository.existsById(idLivro);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitulo(String titulo) {
        return livroRepository.findByTituloIgnoreCase(titulo).isPresent();
    }


    @Override
    @Transactional(readOnly = true)
    public Livro pegarReferenciaPorId(Long idLivro) {
        var livroEntity = livroRepository.findById((idLivro)).orElseThrow(() -> new LivroInvalidoException("Livro não encontrado: ID: " + idLivro));
        return livroMapper.entityToDomain(livroEntity);
    }


}
