package com.pedrohenrique.bibliotecavirtual.adapter.output.repository;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
