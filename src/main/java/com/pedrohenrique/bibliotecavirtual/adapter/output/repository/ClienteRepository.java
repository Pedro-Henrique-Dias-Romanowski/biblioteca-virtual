package com.pedrohenrique.bibliotecavirtual.adapter.output.repository;

import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity ,Long> {

    Optional<ClienteEntity> findByEmailIgnoreCase(String email);
}
