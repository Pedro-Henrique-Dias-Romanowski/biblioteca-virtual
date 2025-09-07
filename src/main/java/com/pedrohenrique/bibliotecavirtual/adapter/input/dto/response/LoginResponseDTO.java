package com.pedrohenrique.bibliotecavirtual.adapter.input.dto.response;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        String token,
        LocalDateTime dataHoraLogin
) {
}
