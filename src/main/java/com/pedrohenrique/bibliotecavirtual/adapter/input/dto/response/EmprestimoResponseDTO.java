package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response;

import java.time.LocalDate;
import java.util.List;

public record EmprestimoResponseDTO(
        Long idEmprestimo,
        Long idCliente,
        List<Long> livros,
        LocalDate dataEmprestimo,
        LocalDate dataDevolucao,
        Boolean ativo
) {
}
