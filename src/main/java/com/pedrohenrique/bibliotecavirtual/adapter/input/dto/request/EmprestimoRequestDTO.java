package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import java.time.LocalDate;
import java.util.List;

public record EmprestimoRequestDTO(
        Long clienteId,
        List<Long> idLivro,
        LocalDate dataDevolucao
){
}
