package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record EmprestimoRequestDTO(
        Long clienteId,
        List<Long> livros,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataDevolucao
){
}
