package com.pedrohenrique.bibliotecavirtual.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class Emprestimo {

    private Long id;
    private Long idLivro;
    private Long idUsuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
}
