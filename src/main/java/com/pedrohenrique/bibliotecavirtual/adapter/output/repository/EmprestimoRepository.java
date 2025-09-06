package com.pedrohenrique.bibliotecavirtual.adapter.output.repository;

import com.pedrohenrique.bibliotecavirtual.adapter.output.entity.EmprestimoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<EmprestimoEntity, Long> {
}
