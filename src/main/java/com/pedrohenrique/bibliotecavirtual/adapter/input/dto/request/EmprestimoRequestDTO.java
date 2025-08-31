package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import java.time.LocalDate;

public record EmprestimoRequestDTO(
        Long idUsuario,
        Long idLivro,
        LocalDate dataEmprestimo,
        LocalDate dataDevolucao
){
}
