package com.pedrohenrique.bibliotecavirtual.adapter.output.repository;

import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivroRepository extends JpaRepository<LivroEntity, Long> {

    boolean existsById(Long id);
    Optional<LivroEntity> findById(Long id);

    Optional<LivroEntity> findByTituloIgnoreCase(String titulo);

}
