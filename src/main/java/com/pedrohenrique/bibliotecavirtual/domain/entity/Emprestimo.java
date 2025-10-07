package com.pedrohenrique.bibliotecavirtual.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class Emprestimo {

    private Long id;
    private List<Long> livros;
    private Long clienteId;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
}
