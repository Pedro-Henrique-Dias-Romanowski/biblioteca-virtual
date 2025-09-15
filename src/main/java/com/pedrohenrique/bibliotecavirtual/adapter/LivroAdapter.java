package com.pedrohenrique.bibliotecavirtual.adapter;

import com.pedrohenrique.bibliotecavirtual.adapter.input.mappers.LivroMapper;
import com.pedrohenrique.bibliotecavirtual.adapter.output.repository.LivroRepository;
import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import com.pedrohenrique.bibliotecavirtual.domain.port.output.LivroOutputPort;
import org.springframework.stereotype.Component;

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

    @Override
    public Livro cadastrarLivro(Livro livro){
        var livroEntity = livroMapper.toEntity(livro);
        livroEntity.setDisponivel(true);
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

    @Override
    public void removerLivro(Long idLivro) {
        livroRepository.deleteById(idLivro);
    }

    @Override
    public boolean existeLivroPorId(Long idLivro) {
        return livroRepository.existsById(idLivro);
    }

    @Override
    public boolean existeLivroPorTitulo(String titulo) {
        return livroRepository.findByTituloIgnoreCase(titulo).isPresent();
    }
}
