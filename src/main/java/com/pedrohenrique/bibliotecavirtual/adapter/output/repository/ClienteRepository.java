package com.pedrohenrique.bibliotecavirtual.adapter.output.repository;

import com.pedrohenrique.bibliotecavirtual.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente ,Long> {
}
