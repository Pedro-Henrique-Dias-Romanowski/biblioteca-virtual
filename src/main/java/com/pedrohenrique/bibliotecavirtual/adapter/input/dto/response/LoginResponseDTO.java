package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response;

import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        String token,
        LocalDateTime dataHoraLogin,
        Perfil perfilAtribuido
) {
}
