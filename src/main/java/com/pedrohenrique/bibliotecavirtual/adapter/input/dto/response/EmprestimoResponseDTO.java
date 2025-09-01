package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response;

import java.time.LocalDate;

public record EmprestimoResponseDTO(
        Long idEmprestimo,
        Long idLivro,
        LocalDate dataEmprestimo,
        LocalDate dataDevolucao
) {
}
