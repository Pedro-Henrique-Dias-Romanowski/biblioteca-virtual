package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.request;

import jakarta.validation.constraints.NotNull;

public record DevolucaoEmprestimoRequestDTO(

        @NotNull(message = "ID do cliente não pode ser nulo")
        Long idCliente,

        @NotNull(message = "ID do empréstimo não pode ser nulo")
        Long idEmprestimo
) {
}
