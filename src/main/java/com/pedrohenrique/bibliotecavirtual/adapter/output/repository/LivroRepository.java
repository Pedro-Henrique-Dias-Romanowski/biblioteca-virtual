package com.pedrohenrique.bibliotecavirtual.adapter.output.repository;

import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.LivroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<LivroEntity, Long> {
}
