package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.LivroMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.LivroRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroInvalidoException;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class LivroAdapter implements LivroOutputPort {

    private final LivroRepository livroRepository;

    private final LivroMapper livroMapper;

    public LivroAdapter(LivroRepository livroRepository, LivroMapper livroMapper) {
        this.livroRepository = livroRepository;
        this.livroMapper = livroMapper;
    }

    @Transactional
    @Override
    public Livro cadastrarLivro(Livro livro){
        var livroEntity = livroMapper.toEntity(livro);
        var livroSalvo = livroRepository.save(livroEntity);

        return livroMapper.entityToDomain(livroSalvo);
    }


    @Override
    public List<Livro> visualizaTodosOsLivros() {
        return livroRepository.findAll()
                .stream()
                .map(livroMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<Livro> buscarLivroPorId(Long idLivro){
        var livroEntity = livroRepository.findById(idLivro);
        return Optional.ofNullable(livroMapper.entityToDomainOptional(livroEntity));
    }

    @Transactional
    @Override
    public void removerLivro(Long idLivro) {
        livroRepository.deleteById(idLivro);
    }

    @Override
    public boolean existsById(Long idLivro) {
        return livroRepository.existsById(idLivro);
    }

    @Override
    public boolean existsByTitulo(String titulo) {
        return livroRepository.findByTituloIgnoreCase(titulo).isPresent();
    }


    @Override
    public Livro pegarReferenciaPorId(Long idLivro) {
        var livroEntity = livroRepository.findById((idLivro)).orElseThrow(() -> new LivroInvalidoException("Livro não encontrado: ID: " + idLivro));
        return livroMapper.entityToDomain(livroEntity);
    }


}
